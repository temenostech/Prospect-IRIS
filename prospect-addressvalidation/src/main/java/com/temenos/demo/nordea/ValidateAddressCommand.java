package com.temenos.demo.nordea;

import java.util.HashMap;
import java.util.Map;

import org.odata4j.core.OEntity;

import com.temenos.interaction.commands.odata.OEntityTransformer;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.resource.EntityResource;

public class ValidateAddressCommand implements InteractionCommand {

	public ValidateAddressCommand() {}
	
	@SuppressWarnings("unchecked")
	public Result execute(InteractionContext ctx) throws InteractionException {
		ValidateAddress va = new ValidateAddress();
		
		Map<String, Object> properties = null;
		try {
			OEntity entity = ((EntityResource<OEntity>) ctx.getResource()).getEntity();
			properties = new OEntityTransformer().transform(entity);
		} catch (ClassCastException cce) {
			properties = transform(((EntityResource<Entity>) ctx.getResource()).getEntity());
		}
		
		StringBuffer request = new StringBuffer();
		for (String property : properties.keySet()) {
			// vejnavn=Fægangen,husnr=1,etage=1,dør=13,postnr=4180
			String propname = null;
			if (property.equals("Road")) {
				propname = "vejnavn";
			} else if (property.equals("HouseNumber")) {
				propname = "husnr";
			} else if (property.equals("Floor")) {
				propname = "etage";
			} else if (property.equals("Door")) {
				propname = "dør";
			} else if (property.equals("Postcode")) {
				propname = "postnr";
			}
			
			if (propname != null) {
				request.append(propname+"="+properties.get(property).toString()+",");
			}
		}
		String result = va.validate(request.toString());
		if (result != null && (result.equals("OK") || result.contains("\"status\": 1"))) {
			return Result.SUCCESS;
		}
		return Result.INVALID_REQUEST;

	}
	
	private Map<String,Object> transform(Entity entity) {
		Map<String,Object> returnProperties = new HashMap<String,Object>();
		EntityProperties properties = entity.getProperties();
		for (String property : properties.getProperties().keySet()) {
			returnProperties.put(property, properties.getProperty(property).getValue());
		}
		return returnProperties;
	}

}
