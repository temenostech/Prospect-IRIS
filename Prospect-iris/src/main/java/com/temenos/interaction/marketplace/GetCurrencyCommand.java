package com.temenos.interaction.marketplace;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jettison.json.JSONObject;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;

import com.temenos.interaction.commands.odata.CommandHelper;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.odataext.entity.MetadataOData4j;

/**
 * TODO: Document me!
 *
 * @author mjangid
 *
 */
public class GetCurrencyCommand implements InteractionCommand {

	private MetadataOData4j metadataOData4j;


	public GetCurrencyCommand(MetadataOData4j metadataOData4j) {
		this.metadataOData4j = metadataOData4j;
	}

	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		if(ctx == null) {
			return Result.INVALID_REQUEST;
		}
		
		String currencyCode = ctx.getQueryParameters().getFirst("CurrencyCode");
		if (currencyCode == null || currencyCode.isEmpty())
			currencyCode = ctx.getPathParameters().getFirst("id");
		
		String currencyValue = getCurrencyValue(currencyCode);

		List<OProperty<?>> props = new ArrayList<OProperty<?>>();	
		props.add(OProperties.string("CurrencyCode", currencyCode));
		props.add(OProperties.string("CurrencyValue", currencyValue));

		OEntityKey key = OEntityKey.create(currencyCode);
		OEntity entity = OEntities.create( metadataOData4j.getEdmEntitySetByEntityName("CurrencyRate"), key, props, new ArrayList<OLink>());

		ctx.setResource(CommandHelper.createEntityResource(entity));
		
		return Result.SUCCESS;
	}

	/**
	 * @param currencyCode
	 * @return currency rate
	 */
	private String getCurrencyValue(String currencyCode)  {
		try {
			// get the currency
			String httpURL = "http://api.fixer.io/latest?base=GBP&symbols=" + currencyCode;
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(httpURL);
			client.executeMethod(method);
			String jsonResponse = method.getResponseBodyAsString();
			
			//parse the currency
			JSONObject jo = new JSONObject(jsonResponse);
			String rate = jo.getString("rates");
			JSONObject keyVal = new JSONObject(rate);
			return keyVal.getString(currencyCode);
		} catch (Exception e) {
			return "empty";
		}		
	}

}
