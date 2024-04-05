var read = require('read-file');
const axios=require('axios');
var ip='192.168.1.234';
let port=3040;
//var endpoint=[];
// read all lines:
read('C:/Users/alexm/Desktop/thingweb.node-wot/examples/prova/fonti.txt','utf-8', function(err, buffer) {
  var s=buffer.split("\n");
  for(var i=0;i<s.length;i++){
    //console.log("URL: "+s[i]);
      WoTHelpers.fetch(s[i]).then(async (td) => {
          // using await for serial execution (note 'async' in then() of fetch())
          try {
              let thing = await WoT.consume(td);
              let http = require("http");
              const url = require('url');
              var fs=require('fs');
              console.info("==========");
              console.info(td);
              console.info("==========");
              let server=http.createServer(async (req,resp)=>{
                const queryObject = url.parse(req.url,true).query;
                console.log("query: ",queryObject['action']);
                console.log("Stringa: "+String(queryObject['action']));
                if(queryObject['action']!=undefined){
                  let val=await thing.invokeAction(String(queryObject['action']));
                  resp.writeHead(200, {'Content-Type': 'text/plain'});
                  console.log("Server attivo");
                  resp.end("Risultato azione "+String(queryObject['action'])+" : "+val);
                  console.log("VAL azione "+queryObject['action']+" : "+val);
                }
                if(queryObject['property']!=undefined){
                  let val=await thing.readProperty(String(queryObject['property']));
                  resp.writeHead(200, {'Content-Type': 'text/plain'});
                  console.log("Server attivo");
                  resp.end("Risultato proprietà "+String(queryObject['property'])+" : "+val);
                  console.log("VAL proprietà "+queryObject['property']+" : "+val);
                }
                if(queryObject['event']!=undefined){
                  thing.subscribeEvent(String(queryObject['event']),(val)=>{
                    resp.writeHead(200, {'Content-Type': 'text/plain'});
                    console.log("Server attivo");
                    resp.end("Risultato evento "+String(queryObject['event'])+" : "+val);
                    console.log("VAL evento "+queryObject['event']+" : "+val);
                  });
                }
              }).listen(port,ip,(error)=>{
                 if(error){
                   return console.log("Errore: ",error);
                 }
                 else{
                   return console.log("Il server funziona all'indirizzo: ",server.address().address+":"+server.address().port);
                 }
              });
              port++;
                       /*fs.appendFile('C:/Users/alexm/Desktop/thingweb.node-wot/examples/prova/endpoint.txt',server.address().address+":"+server.address().port+"\n",function (err) {
                        if (err){
                          console.log("Errore: "+err);
                          throw err;
                        }
                        else{
                          console.log('Saved!');
                        }
                      });*/
                  /*    return console.log("Il server funziona all'indirizzo: ",server.address().address+":"+server.address().port);
                    }
                });
              }*/
              //Service Registry
              /*axios.post('http://137.204.57.93:8443/serviceregistry/mgmt', {
                    serviceDefinition: thing.title,
                    providerSystem:{
                      systemName: thing.title,
                      address: ip,
                      port: port
                    },
                    secure: 'NOT_SECURE',
                    serviceUri:'prova',
                    interfaces:['HTTPS-INSECURE-JSON']
                  })
                  .then(function (response) {
                    console.log(response);
                  })
                  .catch(function (error) {
                    console.log(error);
                  });*/
              /*await read('C:/Users/alexm/Desktop/thingweb.node-wot/examples/prova/endpoint.txt','utf-8', function(err, buffer) {
                console.log(buffer);
                var e=buffer.split("\n");
                console.log(e);
                var r=e[0].split(":");
                console.log(r);
                for(var i=0;i<e.length;i++){
                  r=e[i].split(":");
                }
                /*for(var i=0;i<r.length;i++){
                  console.log("E: ",r[i]);
                }

              });*/
          }
          catch (err) {
              console.error("Script error:", err);
          }
      }).catch((err) => { console.error("Fetch error:", err);
     });

 }

});
