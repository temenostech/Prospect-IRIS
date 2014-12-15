package com.temenos.demo.nordea;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.EntityResource;

public class TestValidateAddressCommand {

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws Exception {
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), mock(MultivaluedMap.class), queryParams, mock(ResourceState.class), mock(Metadata.class));

        EntityProperties properties = new EntityProperties();
        properties.setProperty(new EntityProperty("Road", "Fægangen"));
        properties.setProperty(new EntityProperty("HouseNumber", "1"));
        properties.setProperty(new EntityProperty("Floor", "1"));
        properties.setProperty(new EntityProperty("Door", "13"));
        properties.setProperty(new EntityProperty("Postcode", "4180"));
        Entity entity = new Entity("Customer", properties);
        ctx.setResource(new EntityResource<Entity>(entity));
        ValidateAddressCommand command = new ValidateAddressCommand();
        InteractionCommand.Result result = command.execute(ctx);
        
        assertEquals(Result.SUCCESS, result);
        
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmptyDoNothing() throws Exception {
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
        InteractionContext ctx = new InteractionContext(mock(UriInfo.class), mock(HttpHeaders.class), mock(MultivaluedMap.class), queryParams, mock(ResourceState.class), mock(Metadata.class));

        EntityProperties properties = new EntityProperties();
        properties.setProperty(new EntityProperty("Road", ""));
        properties.setProperty(new EntityProperty("HouseNumber", ""));
        properties.setProperty(new EntityProperty("Floor", ""));
        properties.setProperty(new EntityProperty("Door", ""));
        properties.setProperty(new EntityProperty("Postcode", ""));
        Entity entity = new Entity("Customer", properties);
        ctx.setResource(new EntityResource<Entity>(entity));
        ValidateAddressCommand command = new ValidateAddressCommand();
        InteractionCommand.Result result = command.execute(ctx);
        
        assertEquals(Result.SUCCESS, result);
        
	}

}
