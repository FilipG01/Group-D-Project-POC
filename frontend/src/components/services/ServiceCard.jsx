function ServiceCard({ service }) {
    return (
        <article className="service-card">
      <span className="service-number">
        {String(service.id).padStart(2, "0")}
      </span>

            <h3>{service.title}</h3>

            <p>{service.description}</p>
        </article>
    )
}

export default ServiceCard