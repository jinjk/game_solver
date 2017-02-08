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

app = Flask(__name__)
CORS(app)

SOLVER_URL = 'http://10.0.2.2:8080/solver'

@app.route('/img', methods=['POST'])
def read_mat():
    file = request.files['file']
    data = file.read()
    file.close()
    mat = ip.parse_from_data(data)
    mat_obj = {'mat': mat}
    mat_str = json.dumps(mat_obj)
    print(mat_str)
    headers = {'Content-Type':'application/json'} 
    resp = rq.post(SOLVER_URL, headers = headers, data = mat_str)
    
    return (resp.text, resp.status_code, resp.headers.items())

@app.route('/img', methods=['GET'])
def greeting():
    return 'greeting'



if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=False, threaded=True)
