'use strict'

const request = require('request');
const CLIENT_ID = 'k74e5lkgzjjjwow'
const CLIENT_SECRET = 'pq0sj4r5ku0ofbc' 

module.exports = (app) => {

    app.get('/loginDropBox', loginDropBox)
    app.post('/save', saveDocInDropBox)


    function loginDropBox(req, resp) {
        resp.redirect(302,
            // authorization endpoint
            'https://www.dropbox.com/oauth2/authorize?'
            // client id
            + 'client_id='+ CLIENT_ID +'&'
            // scope "openid email"
            //+ 'scope=openid%20email&'
            // responde_type for "authorization code grant"
            + 'response_type=code&'
            // redirect uri used to register RP
            + 'redirect_uri=http://localhost:3000/cb-dropbox')
    }

    app.get('/cb-dropbox', (req, resp) => {
        console.log('making request to token endpoint')
        // https://www.npmjs.com/package/request#examples
        // content-type: application/x-www-form-urlencoded (URL-Encoded Forms)

        //TODO -> adaptar ap dropbox

        
        request
            .post(
                { 
                    url: 'https://www.googleapis.com/oauth2/v3/token',
                    // body parameters
                    form: {
                        code: req.query.code,
                        client_id: CLIENT_ID,
                        client_secret: CLIENT_SECRET,
                        redirect_uri: 'http://localhost:3000/cb-dropbox',
                        grant_type: 'authorization_code'
                    }
                }, 
                function(err, httpResponse, body){
                    console.log(body);
                    // send code and id_token to user-agent, just for debug purpose
                    var json_response = JSON.parse(body);
                    resp.send(
                        '<div> callback with code = ' + req.query.code + '</div>' +
                        '<div> id_token = ' + json_response.id_token + '</div>'
                    );
                }
            );
            
    })


    function saveDocInDropBox(req, resp) {
    
    }


    

}

