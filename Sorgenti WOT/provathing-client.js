WoTHelpers.fetch("coap://localhost:5686/provathing").then(async (td) => {
    // using await for serial execution (note 'async' in then() of fetch())
    try {
        let thing = await WoT.consume(td);
        console.info("=== TD ===");
        console.info(td);
        console.info("==========");
        //Indirizzo attuale
        console.log("IP: "+td.actions.leggi.forms[0]["htv:methodName"]);
        // stringa attuale
        let read1 = await thing.readProperty("valore");
        console.info("Valore attuale: ", read1);
        //modifica
    //    await thing.invokeAction("modifica","ciaone",{uriVariables:{'val':"provami"}});
        let inc1 = await thing.readProperty("valore");
        console.info("Nuovo valore: ", inc1);
        //leggi
        await thing.invokeAction("leggi");
        let k= await thing.readProperty("email");
        console.info("email: "+k);
        let r= await thing.invokeAction("ottieni","pippo");
        console.log("R: ",r);
        /*let em = await thing.readProperty("em");
        console.info("Nuovo valore: ", inc1);*/
    }
    catch (err) {
        console.error("Script error:", err);
    }
}).catch((err) => { console.error("Fetch error:", err); });
