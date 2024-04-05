const axios = require('axios');
let r;

//servient
WoT.produce({
    title: "provathing3",
    description: "thing prova",
    "@context": ["https://www.w3.org/2019/wot/td/v1", { "iot": "http://example.org/iot" }],
    "securityDefinitions": { "nosec_sc": { "scheme": "nosec" }},
    "security": "nosec_sc",
    properties: {
        nome: {
            type: "string",
            description: "nome",
            observable: true
      },
      cognome: {
          type: "string",
          description: "cognome",
          observable: true
    }
    },
    actions: {
      mostra: {
          description: "ottieni nome e cognome",
      }
    },
    events: {
    }
})
    .then((thing) => {
        console.log("Creata thing di nome: " + thing.getThingDescription().title);
      r=thing.getServient().servers[0].port;
      thing.writeProperty("nome","Alessio");
      thing.writeProperty("cognome","Di Dio");
        thing.setActionHandler("mostra",async (params) => {
          var n;
          var c;
          n=await thing.readProperty("nome").then((val)=>{
            return val;
          });
          c=await thing.readProperty("cognome").then((val)=>{
            return val;
          });
          return ("Nome e cognome: "+n+" "+c);
        });
          thing.expose().then(async ()=>{
            let indirizzo;
            await axios.get("http://localhost:"+r)
            .then(function (response) {
              console.log("SI: "+response.data[response.data.length-1]);
              indirizzo=response.data[response.data.length-1];
            })
            .catch(function (error) {
              console.log(error);
            });
            console.log("R: ",r);
            console.log("Indirizzo:",indirizzo);
            WoTHelpers.fetch("http://localhost:8085/td").then(async (td) => {
                // using await for serial execution (note 'async' in then() of fetch())
                try {
                    let thing = await WoT.consume(td);
                    await thing.invokeAction("aggiungi",undefined, {"uriVariables":{"indirizzo":indirizzo}});
                    console.log("OK");
                }catch(err){
                  console.error("Script error:", err);
                }
            }).catch((err) => { console.error("Fetch error:", err);
          });
          });
      })
    .catch((e) => {
        console.log("EEEE "+e);
});
