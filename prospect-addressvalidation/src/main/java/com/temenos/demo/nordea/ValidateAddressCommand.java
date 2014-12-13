package com.temenos.demo.nordea;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.resource.EntityResource;

public class ValidateAddressCommand implements InteractionCommand {

	@SuppressWarnings("unchecked")
	public Result execute(InteractionContext ctx) throws InteractionException {
		ValidateAddress va = new ValidateAddress();
		
		EntityResource<Entity> resource = (EntityResource<Entity>) ctx.getResource();
		Entity entity = resource.getEntity();
		EntityProperties properties = entity.getProperties();
		StringBuffer request = new StringBuffer();
		for (String property : properties.getProperties().keySet()) {
			request.append(property+"="+properties.getProperty(property).getValue()+",");
		}
		String result = va.validate(request.toString());
		if (result.contains("\"status\": 1")) {
			return Result.SUCCESS;
		}
		return Result.INVALID_REQUEST;
	}

}
