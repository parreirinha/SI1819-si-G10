'use strict'

const express = require('express')
const app = express()

const DropBox = require('./DropBox')
const GoogleDrive = require('./GoogleDrive')
const port = 3000

DropBox(app)
GoogleDrive(app)

app.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }
    console.log(`server is listening on ${port}`)
})
