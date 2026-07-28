import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { listAppointments } from "../../api/apiAppointments";
import "../../styles/therapists/therapistAppointments.css";

  function formatDateTime(value) {
      return new Intl.DateTimeFormat("en-IE", {
          dateStyle: "medium",
          timeStyle: "short",
      }).format(new Date(value));
  }

  function getDurationMinutes(start, end) {
      const startDate = new Date(start);
      const endDate = new Date(end);

      return Math.round((endDate - startDate) / 60000);
  }

  function formatLabel(value) {
      return value ? value.replace("_", " ") : "";
  }

  function TherapistAppointments() {
      const [appointments, setAppointments] = useState([]);
      const [loading, setLoading] = useState(true);
      const [error, setError] = useState("");

      useEffect(() => {
          async function loadAppointments() {
              setLoading(true);
              setError("");

              try {
                  const loadedAppointments = await listAppointments();

                  const sortedAppointments = [...loadedAppointments].sort(
                      (a, b) =>
                          new Date(a.scheduledStart) -
                          new Date(b.scheduledStart)
                  );

                  setAppointments(sortedAppointments);
              } catch (err) {
                  setError(err.message || "Could not load appointments");
              } finally {
                  setLoading(false);
              }
          }

          loadAppointments();
      }, []);

      return (
          <main className="therapist-appointments-page">
              <header className="therapist-appointments-header">
                  <Link to="/therapist" className="therapist-appointments-back">
                      Back to dashboard
                  </Link>

                  <p className="section-label">Appointments</p>
                  <h1>Requested Sessions</h1>
                  <p>
                      View appointment requests from clients who have booked
                      sessions with you.
                  </p>
              </header>

              {loading && (
                  <p className="therapist-appointments-status">
                      Loading appointments...
                  </p>
              )}

              {error && (
                  <p className="therapist-appointments-status therapist-appointments-error">
                      {error}
                  </p>
              )}

              {!loading && !error && appointments.length === 0 && (
                  <section className="therapist-appointments-empty">
                      <h2>No appointments yet</h2>
                      <p>
                          Client appointment requests will appear here once they
                          book a session.
                      </p>
                  </section>
              )}

              {!loading && !error && appointments.length > 0 && (
                  <section className="therapist-appointments-list">
                      {appointments.map((appointment) => (
                          <article
                              key={appointment.id}
                              className="therapist-appointment-card"
                          >
                              <div className="therapist-appointment-card-main">
                                  <div>
                                      <p className="therapist-appointment-label">
                                          Client
                                      </p>

                                      <h2>
                                          {appointment.clientFirstName}{" "}
                                          {appointment.clientLastName}
                                      </h2>
                                  </div>

                                  <span
                                      className={`therapist-appointment-status therapist-appointment-
                                      status--${appointment.status.toLowerCase()}`}
                                  >
                                      {formatLabel(appointment.status)}
                                  </span>
                              </div>

                              <dl className="therapist-appointment-details">
                                  <div>
                                      <dt>Start</dt>
                                      <dd>
                                          {formatDateTime(
                                              appointment.scheduledStart
                                          )}
                                      </dd>
                                  </div>

                                  <div>
                                      <dt>Duration</dt>
                                      <dd>
                                          {getDurationMinutes(
                                              appointment.scheduledStart,
                                              appointment.scheduledEnd
                                          )}{" "}
                                          minutes
                                      </dd>
                                  </div>

                                  <div>
                                      <dt>Location</dt>
                                      <dd>
                                          {formatLabel(
                                              appointment.locationType
                                          )}
                                      </dd>
                                  </div>
                              </dl>

                              {appointment.clientNotes && (
                                  <div className="therapist-appointment-notes">
                                      <p className="therapist-appointment-label">
                                          Client notes
                                      </p>

                                      <p>{appointment.clientNotes}</p>
                                  </div>
                              )}
                          </article>
                      ))}
                  </section>
              )}
          </main>
      );
  }

  export default TherapistAppointments;