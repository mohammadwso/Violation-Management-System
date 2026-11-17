"""Mock endpoint that accepts 'accepted violations' from our system and just logs them."""
import json
import os
from http.server import BaseHTTPRequestHandler, HTTPServer

PORT = int(os.getenv("PORT", "8080"))


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        body = self.rfile.read(int(self.headers.get("Content-Length", 0)))
        try:
            data = json.loads(body)
        except json.JSONDecodeError:
            data = body.decode(errors="replace")
        print(f"[receiver] {self.path} <- {data}", flush=True)
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(b'{"status":"received"}')

    do_PUT = do_POST

    def log_message(self, *args):
        pass  # silence default access log


print(f"[receiver] listening on :{PORT}", flush=True)
HTTPServer(("0.0.0.0", PORT), Handler).serve_forever()
