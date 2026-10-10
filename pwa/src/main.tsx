import React from "react";
import ReactDOM from "react-dom/client";
import { registerSW } from "virtual:pwa-register";
import { App } from "@/app/App";
import "@/components/arc/foundation.css";
import "@/styles/app.css";

registerSW({ immediate: true });

const rootElement = document.getElementById("root");
if (!rootElement) throw new Error("Uygulama kök öğesi bulunamadı.");
ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
