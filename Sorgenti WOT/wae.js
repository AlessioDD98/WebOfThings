/*
# expose
node packages\cli\dist\cli.js examples\scripts\counter.js
# consume
node packages\cli\dist\cli.js --clientonly examples\scripts\counter-client.js
*/

let http = require("http");
const url = require('url');
const exec = require('child_process').exec;
const process = require('process');
const axios = require('axios');
var read = require('read-file');
var ip='192.168.1.234';
let port=3056;
var id=[];
var pn;

function ATM(s){
  WoTHelpers.fetch(s).then(async (td) => {
      try {
          let thing = await WoT.consume(td);
          console.info("==========");
          console.info(td);
          console.info("==========");
          let server=http.createServer(async (req,resp)=>{
            const queryObject = url.parse(req.url,true).query;
            if(queryObject['action']!=undefined){
              var ris="";
              var r;
              let val=await thing.invokeAction(String(queryObject['action']));
              console.log("Server attivo");
              if(val==undefined){
                resp.end("Risultato azione "+String(queryObject['action'])+" ASSENTE");
                console.log("VAL azione "+queryObject['action']+" ASSENTE");
              }
              else{
                if(typeof val==='object'){
                  resp.writeHead(200, {'Content-Type': 'application/json'});
                  var presente=false;
                  console.log("oggetto");
                  var valk=Object.keys(val);
                  console.log("VALK: ",valk);
                  resp.write(val);
                  resp.end();
                }
                else{
                  val='{"'+String(queryObject['action'])+'":"'+val+'"}';
                  resp.end(val);
                  console.log("VAL azione' "+queryObject['action']+" : "+val);
                }
              }
            }
            if(queryObject['property']!=undefined){
              var ris="";
              let val=await thing.readProperty(String(queryObject['property']));
              console.log("Chiavi: ",val);
              console.log("Server attivo");
              if(val==undefined){
                resp.end("Risultato proprieta' "+String(queryObject['property'])+" ASSENTE");
                console.log("VAL proprieta' "+queryObject['property']+" ASSENTE");
              }
              else{
                if(typeof val==='object'){
                  resp.writeHead(200, {'Content-Type': 'application/json'});
                  if(String(queryObject['property'])=="AccelerationSamples"||String(queryObject['property'])=="GyroscopeSamples"){
                    val["0"].nome=td.title;
                  }
                  else{
                    val.nome=td.title;
                  }
                  resp.write(JSON.stringify(val));
                  resp.end();
                  var presente=false;
                  console.log("oggetto");
                  console.log("Val: ",JSON.stringify(val));
                  var valk=Object.keys(val);
                  console.log("VALK:",valk);
                }
                else{
                  var risposta='{"'+String(queryObject['property'])+'":"'+val+'","nome":'+'"'+td.title+'"}';
                  resp.end(risposta);
                  console.log("VAL else proprieta' "+queryObject['property']+" : "+risposta);
                }

              }
            }
            if(queryObject['event']!=undefined){
              thing.subscribeEvent(String(queryObject['event']),(val)=>{
                resp.writeHead(200, {'Content-Type': 'application/json'});
                console.log("Server attivo");
                if(val==undefined){
                  resp.end("Risultato evento "+String(queryObject['event'])+" ASSENTE");
                  console.log("VAL evento "+queryObject['event']+" ASSENTE");
                }
                else{
                  resp.end("Risultato evento "+String(queryObject['event'])+" : "+val);
                  console.log("VAL evento "+queryObject['event']+" : "+val);
                }
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
          registra(JSON.stringify(td),ip,port);
          port++;
      }catch(err){
        console.error("Script error:", err);
      }
  }).catch((err) => { console.error("Errore durante la Fetch:", err);
  });
}

function registra(thing,ip,port){
  let thingO=JSON.parse(thing);
  axios.post('http://137.204.57.93:8443/serviceregistry/mgmt', {
        serviceDefinition: thingO.title,
        providerSystem:{
          systemName: thingO.title,
          address: ip,
          port: port
        },
        secure: 'NOT_SECURE',
        metadata: {
          additionalProp1: thing
        },
        interfaces:['HTTPS-INSECURE-JSON']
      })
      .then(function (response) {
        id.push(response.data.id);
        console.log("ID: "+response.data.id);
        console.log("Registrazione effettuata");
        //console.log(response);
      })
      .catch(function (error) {
        console.log("Errore");
        //console.log(error);
      });
}

function deregistra(){
  return async function() {
    for(var i=0;i<id.length;i++){
      await axios({
        method:'delete',
        url:'http://137.204.57.93:8443/serviceregistry/mgmt/'+id[i]
      })
      .then(response=>{
        console.log("Rimosso servizio con id "+id[i]);
      })
      .catch(error=>{
        console.log("ERRORE: ",error);
      });
    }
    console.log("Esco");
    process.exit();
  }
}

//servient
WoT.produce({
    title: "wae",
    description: "wae",
    "@context": ["https://www.w3.org/2019/wot/td/v1", { "iot": "http://example.org/iot" }],
    "securityDefinitions": { "nosec_sc": { "scheme": "nosec" }},
    "security": "nosec_sc",
    properties: {
        listaThing: {
            type: "string",
            description: "lista thing conosciute",
            observable: true
      }
    },
    actions: {
      query: {
        description: "ottieni lista dei servizi ArrowHead"
      },
      ricerca:{
        description:"cerca nuove thing basandoti sulla lista di web thing già conosciute"
      }
    },
    events: {
    }
})
    .then((thing) => {
        process.on('SIGTERM',deregistra());
        process.on('SIGINT',deregistra());
        console.log("Creata thing di nome: " , thing.getThingDescription().title);
        console.log("FORSE MI SENTO FORTUNATO");
        thing.setActionHandler("query", (params) => {
          return axios.get('http://137.204.57.93:8443/serviceregistry/mgmt/')
          .then((response)=>{
            console.log("RISPOSTA: ");
            console.log(response.data);
            return response.data;
          })
          .catch((error)=>{
            console.log("Errore: ");
            console.log(error);
          });
        });
        thing.setActionHandler("ricerca",async (params)=>{
          let l=await thing.readProperty("listaThing").then((val)=>{
            return val;
          });
          let b;
          await WoTHelpers.fetch("http://localhost:8085/td").then(async (td) => {
              // using await for serial execution (note 'async' in then() of fetch())
              try {
                  let thing = await WoT.consume(td);
                  b=await thing.readProperty("listaThing");
                  console.log("OK: ",b);
              }catch(err){
                console.error("Script error:", err);
              }
          }).catch((err) => { console.error("Fetch error:", err);
          });
            var s=b.split(/\r?\n/);
            var lista;
            console.log("lista:", l);
            if(l==null){
              console.log("Registrazione prima thing");
              ATM(s[0]);
              await thing.writeProperty("listaThing",s[0]);
            }
            for(var i=0;i<s.length;i++){
              l=await thing.readProperty("listaThing").then((val)=>{
                return val;
              });
              lista=l.split(/\r?\n/);
              console.log("DIM: "+s.length);
              console.log("S: "+s[i]);
              console.log("Lista splittata: "+lista);
              if(!lista.includes(s[i])){
                  console.log("ASSENTE");
                  ATM(s[i]);
                  await thing.writeProperty("listaThing",l+"\n"+s[i]);
              }
            }
        });
        thing.expose().then(()=>{
          //setInterval(() => {
            const child = exec('node packages/cli/dist/cli.js --clientonly examples/prova/wotClient.js',
            (error, stdout, stderr) => {
              console.log(`stdout: ${stdout}`);
              console.log(`stderr: ${stderr}`);
              if (error !== null) {
                console.log(`exec error: ${error}`);
              }
            });
        //  }, 5000);
          console.log("OK");
        });
      })
    .catch((e) => {
        console.log("EEEE "+e);
});
