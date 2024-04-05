const axios = require('axios');

WoT.produce({
    title: "td",
    description: "td",
    "@context": ["https://www.w3.og/2019/wot/td/v1", { "iot": "http://example.org/iot" }],
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
      aggiungi: {
        description: "aggiungi thing alla lista thing conosciute",
        uriVariables: {
            indirizzo: { "type": "string" }
        }
      }
    },
    events: {
    }
})
    .then((thing) => {
        console.log("Creata thing di nome: " , thing.getThingDescription().title);
        thing.setActionHandler("aggiungi", async (params,options) => {
          let l=await thing.readProperty("listaThing").then((val)=>{
            return val;
          });
          console.log("L: "+l);
          if(l!= null){
            var lista=l.split(/\r?\n/);
            if(!lista.includes(options.uriVariables.indirizzo)){
              console.log("PARAMS: "+options.uriVariables.indirizzo);
              await thing.writeProperty("listaThing",l+"\n"+options.uriVariables.indirizzo);
              console.log("Thing aggiunta");
            }
            else{
              console.log("Indirizzo già presente");
            }
          }
          else{
            console.log("ELSE");
            console.log("PARAMS: ",options.uriVariables.indirizzo);
            await thing.writeProperty("listaThing",options.uriVariables.indirizzo);
            console.log("Thing aggiunta");
          }
        });
        thing.expose().then(()=>{
          axios.get("http://137.204.143.89:8080/")
          .then(async function (response) {
            var lt=response.data[0]+"\n";
            for(var i=1;i<response.data.length;i++){
              lt+=response.data[i]+"\n";
            }
            console.log("LT: "+lt);
            await thing.writeProperty("listaThing",lt);
            await thing.readProperty("listaThing").then((val)=>{
              return console.log("val:",val);
            });
          })
          .catch(function (error) {
            console.log(error);
          });
          console.log("OK");
        });
      })
    .catch((e) => {
        console.log("Errore Esposizione: "+e);
});
