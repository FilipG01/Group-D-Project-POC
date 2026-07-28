import { useEffect, useState } from "react";
import { createAppointment, listAppointments } from "../../api/apiAppointments";

  function toDateInputValue(date) {
      const copy = new Date(date);
      copy.setMinutes(copy.getMinutes() - copy.getTimezoneOffset());
      return copy.toISOString().slice(0, 10);
  }
  
  function formatAppointmentDate(value) {
      return new Intl.DateTimeFormat("en-IE", {
          dateStyle: "medium",
          timeStyle: "short",
      }).format(new Date(value));
  }

  function ClientAppointments({ conversation }) {
      const [appointments, setAppointments] = useState([]);
      const [loading, setLoading] = useState(true);
      const [submitting, setSubmitting] = useState(false);
      const [error, setError] = useState("");
      const [success, setSuccess] = useState("");

      const [form, setForm] = useState({
          date: toDateInputValue(new Date()),
          startTime: "10:00",
          durationMinutes: "50",
          locationType: "ONLINE",
          clientNotes: "",
      });

      useEffect(() => {
          async function loadAppointments() {
              try {
                  const loadedAppointments = await listAppointments();
                  setAppointments(loadedAppointments);
              } catch (err) {
                  setError(err.message || "couldn't load appointments");
              } finally {
                  setLoading(false);
              }
          }

          loadAppointments();
      }, []);

      function updateField(event) {
          const { name, value } = event.target;

          setForm((currentForm) => ({
              ...currentForm,
              [name]: value,
          }));
      }

      async function handleSubmit(event) {
          event.preventDefault();
          setError("");
          setSuccess("");
          setSubmitting(true);

          try {
              const start = new Date(`${form.date}T${form.startTime}:00`);
              const end = new Date(
                  start.getTime() + Number(form.durationMinutes) * 60 * 1000
              );

              const createdAppointment = await createAppointment({
                  scheduledStart: start.toISOString(),
                  scheduledEnd: end.toISOString(),
                  locationType: form.locationType,
                  clientNotes: form.clientNotes.trim() || null,
              });

              setAppointments((currentAppointments) => [
                  ...currentAppointments,
                  createdAppointment,
              ]);

              setForm((currentForm) => ({
                  ...currentForm,
                  clientNotes: "",
              }));

              setSuccess("Appointment request sent.");
          } catch (err) {
              setError(err.message || "couldn't request appointment");
          } finally {
              setSubmitting(false);
          }
      }

      if (!conversation) {
          return (
              <section className="client-appointments">
                  <h2>Appointments</h2>
                  <p>Select a therapist from the Dashboard before booking an appointment.</p>
              </section>
          );
      }

      const therapistName =
          `${conversation.therapistFirstName} ${conversation.therapistLastName}`;

      return (
          <section className="client-appointments">
              <div className="client-appointments-header">
                  <div>
                      <h2>Appointments</h2>
                      <p>Book an appointment with {therapistName}.</p>
                  </div>
              </div>

              {error && <p className="client-appointments-error">{error}</p>}
              {success && <p className="client-appointments-success">{success}</p>}

              <div className="client-appointments-grid">
                  <form className="client-appointment-form" onSubmit={handleSubmit}>
                      <h3>Request Appointment</h3>

                      <label>
                          Date
                          <input
                              type="date"
                              name="date"
                              value={form.date}
                              onChange={updateField}
                              required
                          />
                      </label>

                      <label>
                          Start time
                          <input
                              type="time"
                              name="startTime"
                              value={form.startTime}
                              onChange={updateField}
                              required
                          />
                      </label>

                      <label>
                          Duration
                          <select
                              name="durationMinutes"
                              value={form.durationMinutes}
                              onChange={updateField}
                          >
                              <option value="30">30 minutes</option>
                              <option value="50">50 minutes</option>
                              <option value="60">60 minutes</option>
                              <option value="90">90 minutes</option>
                          </select>
                      </label>

                      <label>
                          Location
                          <select
                              name="locationType"
                              value={form.locationType}
                              onChange={updateField}
                          >
                              <option value="ONLINE">Online</option>
                              <option value="IN_PERSON">In person</option>
                              <option value="PHONE">Phone</option>
                          </select>
                      </label>

                      <label>
                          Notes
                          <textarea
                              name="clientNotes"
                              value={form.clientNotes}
                              onChange={updateField}
                              placeholder="Anything you want your therapist to know?"
                              rows="5"
                          />
                      </label>

                      <button type="submit" disabled={submitting}>
                          {submitting ? "Sending..." : "Request appointment"}
                      </button>
                  </form>

                  <div className="client-appointment-list">
                      <h3>Your Appointments</h3>

                      {loading ? (
                          <p>Loading appointments...</p>
                      ) : appointments.length === 0 ? (
                          <p>No appointments requested yet.</p>
                      ) : (
                          appointments.map((appointment) => (
                              <article
                                  className="client-appointment-card"
                                  key={appointment.id}
                              >
                                  <strong>
                                      {formatAppointmentDate(appointment.scheduledStart)}
                                  </strong>
                                  <span>{appointment.status}</span>
                                  <p>
                                      {appointment.locationType.replace("_", " ")}
                                  </p>
                              </article>
                          ))
                      )}
                  </div>
              </div>
          </section>
      );
  }

  export default ClientAppointments;