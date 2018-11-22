'use strict'

const request = require('request');


// system variables where RP credentials are stored
//const CLIENT_ID = process.env.CLIENT_ID
//const CLIENT_SECRET = process.env.CLIENT_SECRET

const CLIENT_ID = '275607082629-t824itkcdbsm5adldhlju6kspap3r7od.apps.googleusercontent.com'
const CLIENT_SECRET = 'Oku6DR1Tz2qAIo4DJ9KqUEEF' 



module.exports = (app) => {

    app.get('/', home)
    app.get('/loginGoogle', loginGoogle)
    app.get('/listdocs', listdocs)
    app.get('/copydocs', copydocs)


    function home(req, resp){
        
        const links = '<a href=/loginGoogle>Use Google Account</a><br>'
                    + '<a href=/loginDropBox>Login to DropBox</a><br>'
                    + '<a href=/listdocs>List Documents in Drive</a><br>'
                    + '<a href=/copydocs>Copy document</a>'
        resp.send(links)
    }

    function loginGoogle(req, resp) {
        resp.redirect(302,
            // authorization endpoint
            'https://accounts.google.com/o/oauth2/v2/auth?'
            // client id
            + 'client_id='+ CLIENT_ID +'&'
            // scope "openid email"
            + 'scope=openid%20email&'
            // responde_type for "authorization code grant"
            + 'response_type=code&'
            // redirect uri used to register RP
            + 'redirect_uri=http://localhost:3000/callback')
    }

    app.get('/callback', (req, resp) => {
        console.log('making request to token endpoint')
        // https://www.npmjs.com/package/request#examples
        // content-type: application/x-www-form-urlencoded (URL-Encoded Forms)
        request
            .post(
                { 
                    url: 'https://www.googleapis.com/oauth2/v3/token',
                    // body parameters
                    form: {
                        code: req.query.code,
                        client_id: CLIENT_ID,
                        client_secret: CLIENT_SECRET,
                        redirect_uri: 'http://localhost:3000/callback',
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


    function listdocs(req, resp) {
    
    }

    function copydocs(req, resp) {
    
    }
    

}

