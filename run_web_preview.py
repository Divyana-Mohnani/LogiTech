#!/usr/bin/env python3
"""
Quick Web Preview Server for Retail Apparel Inventory System.
Allows instant testing and demonstration in any browser without waiting for Tomcat setup.
Serves static assets, styles, and simulated interactive pages.
"""

import http.server
import socketserver
import os
import sys

PORT = 8080
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
WEBAPP_DIR = os.path.join(BASE_DIR, "src/main/webapp")

class CustomHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=WEBAPP_DIR, **kwargs)

    def do_GET(self):
        # Redirect root or /inventory to stockUpdate.jsp preview
        if self.path in ("/", "/inventory", "/inventory/", "/dashboard"):
            self.path = "/dashboard.jsp"
        elif self.path in ("/stock", "/stockUpdate"):
            self.path = "/stockUpdate.jsp"
        elif self.path in ("/search",):
            self.path = "/search.jsp"
        elif self.path in ("/label",):
            self.path = "/label.jsp"
        elif self.path in ("/import",):
            self.path = "/import.jsp"
        elif self.path in ("/billing", "/pos"):
            self.path = "/billing.jsp"
        elif self.path.startswith("/receipt"):
            self.path = "/receipt.jsp"
        elif self.path in ("/sales-history", "/history"):
            self.path = "/salesHistory.jsp"
        elif self.path in ("/reports",):
            self.path = "/reports.jsp"
        elif self.path in ("/masters",):
            self.path = "/masters.jsp"
        elif self.path in ("/login",):
            self.path = "/login.jsp"
        return super().do_GET()

def run_server():
    os.chdir(WEBAPP_DIR)
    socketserver.TCPServer.allow_reuse_address = True
    try:
        with socketserver.TCPServer(("", PORT), CustomHandler) as httpd:
            print("====================================================================")
            print(f"  RETAIL APPAREL INVENTORY SYSTEM - INSTANT PREVIEW RUNNING AT:")
            print(f"  --> http://localhost:{PORT}")
            print(f"  Serving views from: {WEBAPP_DIR}")
            print("  Press Ctrl+C to stop.")
            print("====================================================================")
            httpd.serve_forever()
    except OSError as e:
        alt_port = 8000
        with socketserver.TCPServer(("", alt_port), CustomHandler) as httpd:
            print(f"Port {PORT} in use, running on http://localhost:{alt_port}")
            httpd.serve_forever()

if __name__ == "__main__":
    run_server()
