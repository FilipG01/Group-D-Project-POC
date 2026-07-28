import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "../../styles/therapists/therapistAppointments.css";
import {
      listAppointments,
      confirmAppointment,
      cancelAppointment,
} from "../../api/apiAppointments";

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
      const [actionError, setActionError] = useState("");
      const [updatingAppointmentId, setUpdatingAppointmentId] = useState(null);
      const [cancelReasons, setCancelReasons] = useState({});

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

      function updateAppointmentInState(updatedAppointment) {
          setAppointments((currentAppointments) =>
              currentAppointments.map((appointment) =>
                  appointment.id === updatedAppointment.id
                      ? updatedAppointment
                      : appointment
              )
          );
      }

      async function handleConfirm(appointmentId) {
          setActionError("");
          setUpdatingAppointmentId(appointmentId);

          try {
              const updatedAppointment = await confirmAppointment(appointmentId);
              updateAppointmentInState(updatedAppointment);
          } catch (err) {
              setActionError(err.message || "Could not confirm appointment");
          } finally {
              setUpdatingAppointmentId(null);
          }
      }

      function handleCancelReasonChange(appointmentId, value) {
          setCancelReasons((currentReasons) => ({
              ...currentReasons,
              [appointmentId]: value,
          }));
      }

      async function handleCancel(appointmentId) {
          const cancellationReason = cancelReasons[appointmentId]?.trim();

          if (!cancellationReason) {
              setActionError("Cancellation reason is required");
              return;
          }

          setActionError("");
          setUpdatingAppointmentId(appointmentId);

          try {
              const updatedAppointment = await cancelAppointment(
                  appointmentId,
                  cancellationReason
              );

              updateAppointmentInState(updatedAppointment);
          } catch (err) {
              setActionError(err.message || "Could not cancel appointment");
          } finally {
              setUpdatingAppointmentId(null);
          }
      }

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

              {actionError && (
                <p className="therapist-appointments-status therapist-appointments-error">
                {actionError}
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

                              {appointment.cancellationReason && (
                                  <div className="therapist-appointment-notes">
                                      <p className="therapist-appointment-label">
                                          Cancellation reason
                                      </p>

                                      <p>{appointment.cancellationReason}</p>
                                  </div>
                              )}

                              {appointment.status === "REQUESTED" && (
                                  <div className="therapist-appointment-actions">
                                      <button
                                          type="button"
                                          onClick={() =>
                                              handleConfirm(appointment.id)
                                          }
                                          disabled={
                                              updatingAppointmentId ===
                                              appointment.id
                                          }
                                      >
                                          Confirm
                                      </button>

                                      <textarea
                                          value={
                                              cancelReasons[appointment.id] || ""
                                          }
                                          onChange={(event) =>
                                              handleCancelReasonChange(
                                                  appointment.id,
                                                  event.target.value
                                              )
                                          }
                                          placeholder="Cancellation reason"
                                          rows="3"
                                      />

                                      <button
                                          type="button"
                                          onClick={() =>
                                              handleCancel(appointment.id)
                                          }
                                          disabled={
                                              updatingAppointmentId ===
                                              appointment.id
                                          }
                                      >
                                          Cancel
                                      </button>
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