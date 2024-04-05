const axios = require('axios');
let r;

//servient
WoT.produce({
    title: "provathing",
    description: "thing che ottiene un valore da un sito",
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
          description: "ottieni email dal sito"
      },
      ottieni: {
        description: "ottieni valore dal sito",
        uriVariables: {
            parola: { "type": "string" }
        }
      }
    },
    events: {
        mostra: {
            description: "mostra valore",
        }
    }
})
    .then((thing) => {
      var val="ciao a tutti";
      thing.writeProperty("valore",val);
        var email="";
        console.log("Creata thing di nome: " + thing.getThingDescription().title);
      r=thing.getServient().servers[0].port;
        //ottieni dati dalla pagina
        axios({
          method:'get',
          url: 'https://reqres.in/api/users/2',
          headers: { 'User-Agent': 'Console app' }
        })
          .then(function (response) {
          //  console.log("RISPOSTA: ");
            var risp=response;
            //console.log(response.data);
          //  console.log("Struttura risp: "+Object.keys(risp.data));
          //  console.log("tipo: "+typeof(response.data));
            //var rispt=JSON.parse(risp.data);
            //console.log(typeof(rispt));
            //console.log("Nome maicol: "+risp.data.data.email);
            email=risp.data.data.email;
            thing.writeProperty("email", email);
            //console.log("JSON "+rispt);
            console.log("FORSE MI SENTO FORTUNATO");
          })
          .catch(function (error) {
            // handle error
            console.log(error);
        });

        /*thing.readProperty("valore").then((valore)=>{
          console.log("Valore: " +  valore);
        });
        thing.writeProperty("valore",val+" a tutti");
        thing.readProperty("valore").then((valore)=>{
          console.log("Valore: " +  valore);
        });*/
        //set action handler
      /*  thing.setActionHandler("modifica", (params, options) => {
            return thing.readProperty("valore").then((val) => {
                var mod=", come va?";
                if (options && typeof options === 'object' && 'uriVariables' in options) {
                    console.log("options = " + JSON.stringify(options));
                    if ('val' in options['uriVariables']) {
                        let uriVariables = options['uriVariables'];
                        mod = uriVariables['val'];
                    }
                }
                let value = val + mod;
                thing.writeProperty("valore", value);
            });
        });*/
        thing.setActionHandler("ottieni", (params) => {
            return thing.readProperty("valore").then((val) => {
              let ris;
              if(params!=undefined){
                ris=val+params;
              }
              else{
                ris=val+" bella";
              }
              thing.writeProperty("valore",ris);
              console.log("VALORE server: " +  ris);
              return ris;
            });
        });
          thing.setActionHandler("leggi", (params) => {
              return thing.readProperty("email").then((val) => {
                console.log("EMAIL server: " +  val);
                return val;
              });
          });
          thing.expose().then(async()=>{
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
            /*setInterval(() => {
              thing.emitEvent("mostra", "Evento eseguito");
            }, 5000);*/
          });
      })
    .catch((e) => {
        console.log("EEEE "+e);
});
