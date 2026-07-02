import { Routes, Route } from 'react-router-dom'

import Header from './components/Header.jsx'
import Footer from './components/Footer.jsx'

import Home from './pages/Home.jsx'
import About from './pages/About.jsx'
import Services from './pages/Services.jsx'
import Contact from './pages/Contact.jsx'

import ScrollToTop from "./components/shared/ScrollToTop";

import FloatingCallButton from "./components/shared/FloatingCallButton";

function App() {
    return (
        <>
            <ScrollToTop />
            <Header />

            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/about" element={<About />} />
                <Route path="/services" element={<Services />} />
                <Route path="/contact" element={<Contact />} />
            </Routes>

            <FloatingCallButton />

            <Footer />
        </>
    )
}

export default App