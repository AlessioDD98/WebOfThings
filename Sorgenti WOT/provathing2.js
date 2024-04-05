const axios = require('axios');
let r;

WoT.produce({
    title: "provathing2",
    description: "thing prova",
    "@context": ["https://www.w3.org/2019/wot/td/v1", { "iot": "http://example.org/iot" }],
    "securityDefinitions": { "nosec_sc": { "scheme": "nosec" }},
    "security": "nosec_sc",
    properties: {
        valore: {
            type: "string",
            description: "valore ottenuto",
            observable: true
      },
      email: {
          type: "string",
          description: "email maicol",
          observable: true
    }
    },
    actions: {
      leggi: {
          description: "ottieni email dal sito",
      },
      ottieni: {
        description: "ottieni valore dal sito"
      }
    },
    events: {
    }
})
    .then((thing) => {
        console.log("Creata thing di nome: " , thing.getServient().servers[0].port);
        r=thing.getServient().servers[0].port;
        thing.setActionHandler("ottieni", (params) => {
            return thing.readProperty("valore").then((val) => {
              thing.writeProperty("valore","pippo");
              console.log("VALORE server: " +  val);
              return val;
            });
        });
          thing.setActionHandler("leggi", (params) => {
              return thing.readProperty("email").then((val) => {
                console.log("EMAIL server: " +  val);
                return val;
              });
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
            console.log("FORSE MI SENTO FORTUNATO");
          });
      })
    .catch((e) => {
        console.log("EEEE "+e);
});
