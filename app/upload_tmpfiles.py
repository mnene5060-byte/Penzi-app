import sys
import json
import urllib.request
import mimetypes
import uuid

def upload_file(file_path):
    url = "https://tmpfiles.org/api/v1/upload"
    boundary = uuid.uuid4().hex
    
    with open(file_path, 'rb') as f:
        file_bytes = f.read()
        
    filename = "app-debug.apk"
    content_type = "application/vnd.android.package-archive"
    
    body = []
    body.append(f'--{boundary}'.encode('utf-8'))
    body.append(f'Content-Disposition: form-data; name="file"; filename="{filename}"'.encode('utf-8'))
    body.append(f'Content-Type: {content_type}'.encode('utf-8'))
    body.append(b'')
    body.append(file_bytes)
    body.append(f'--{boundary}--'.encode('utf-8'))
    body.append(b'')
    
    data = b'\r\n'.join(body)
    
    req = urllib.request.Request(url, data=data)
    req.add_header('Content-Type', f'multipart/form-data; boundary={boundary}')
    req.add_header('User-Agent', 'Mozilla/5.0 (Android)')
    
    print(f"Uploading {len(data)} bytes to {url}...")
    try:
        with urllib.request.urlopen(req, timeout=120) as resp:
            res_body = resp.read().decode('utf-8')
            print("Response:", res_body)
            try:
                parsed = json.loads(res_body)
                print("UPLOAD_URL:", parsed.get('data', {}).get('url'))
            except Exception:
                pass
    except Exception as e:
        print("Upload error:", e)

if __name__ == '__main__':
    upload_file("app/build/outputs/apk/debug/app-debug.apk")
