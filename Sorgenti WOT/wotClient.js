WoTHelpers.fetch("coap://localhost:5684/wae").then(async (td) => {
    try {
        let thing = await WoT.consume(td);
        await thing.invokeAction("ricerca");
    }catch(err){
      console.error("Script error:", err);
    }
}).catch((err) => { console.error("Fetch error:", err);
});
