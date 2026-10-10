import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { VitePWA } from "vite-plugin-pwa";

const sourcePath = fileURLToPath(new URL("./src", import.meta.url));
const basePath = process.env.BASE_PATH || "/";

export default defineConfig({
  base: basePath,
  plugins: [
    react(),
    VitePWA({
      registerType: "prompt",
      includeAssets: ["favicon.svg", "icons/icon.svg", "icons/icon-192.png", "icons/icon-512.png"],
      manifest: {
        name: "Haftalık Plan · Öğretmen",
        short_name: "Haftalık Plan",
        description: "Öğretmenler için haftalık plan ve sınıf ilerleme çalışma alanı.",
        lang: "tr",
        start_url: ".",
        scope: ".",
        display: "standalone",
        background_color: "#ffffff",
        theme_color: "#182c32",
        icons: [
          { src: "icons/icon-192.png", sizes: "192x192", type: "image/png", purpose: "any" },
          { src: "icons/icon-512.png", sizes: "512x512", type: "image/png", purpose: "any maskable" },
          { src: "icons/icon.svg", sizes: "any", type: "image/svg+xml", purpose: "any" }
        ]
      },
      workbox: {
        globPatterns: ["**/*.{html,js,css,svg,png,json}"],
        navigateFallback: "index.html"
      }
    })
  ],
  resolve: { alias: { "@": sourcePath } }
});
