import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext.jsx";
import { MantineProvider } from "@mantine/core";
import {HelmetProvider} from "react-helmet-async";

import "@mantine/core/styles.css";  
import "./styles/global.css";

import App from "./App.jsx";


ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <HelmetProvider>
            <BrowserRouter>
                <MantineProvider>
                    <AuthProvider>
                        <App />
                    </AuthProvider>
                </MantineProvider>
            </BrowserRouter>
        </HelmetProvider>
    </React.StrictMode>
);