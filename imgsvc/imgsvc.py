import requests as rq
import json
from flask import Flask, request, jsonify
from flask_cors import CORS
from parseimg import imgparser as ip

import logging

# These two lines enable debugging at httplib level (requests->urllib3->http.client)
# You will see the REQUEST, including HEADERS and DATA, and RESPONSE with HEADERS but without DATA.
# The only thing missing will be the response.body which is not logged.
try:
    import http.client as http_client
except ImportError:
    # Python 2
    import httplib as http_client
http_client.HTTPConnection.debuglevel = 1

logging.basicConfig(filename='/var/log/solver/imgsvc.log',level=logging.INFO)

app = Flask(__name__)
CORS(app)

SOLVER_URL = 'http://solversvc:8080/solver'

class PosEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, ip.Pos):
            return obj.__dict__

        return json.JSONEncoder.default(self, obj) 

@app.route('/img', methods=['POST'])
def read_mat():
    file = request.files['file']
    data = file.read()
    file.close()
    headers = {'Content-Type':'application/json'} 
    
    try:
        mat, icons = ip.parse_from_data(data)
    except:
        return ('{"code":4001}', 400, headers)

    mat_len = sum(len(x) for x in mat)
    
    if mat_len != 100:
        return ('{"code":4002}', 400, headers)

    mat_obj = {'mat': mat, 'icons' : icons}
    mat_str = json.dumps(mat_obj, cls = PosEncoder)

    resp = rq.post(SOLVER_URL, headers = headers, data = mat_str)
    
    return (resp.text, resp.status_code, resp.headers.items())

@app.route('/img', methods=['GET'])
def greeting():
    return 'greeting'



if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True, threaded=True)
