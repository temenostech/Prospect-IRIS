package com.temenos.interaction.marketplace;

import java.util.ArrayList;
import java.util.Iterator;
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
public class GetCurrenciesCommand implements InteractionCommand {

	private MetadataOData4j metadataOData4j;

	public GetCurrenciesCommand(MetadataOData4j metadataOData4j) {
		this.metadataOData4j = metadataOData4j;
	}

	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		if (ctx == null) {
			return Result.INVALID_REQUEST;
		}

		String currencyCode = ctx.getQueryParameters().getFirst("CurrencyCode");
		if (currencyCode == null || currencyCode.isEmpty())
			currencyCode = ctx.getPathParameters().getFirst("id");

		ctx.setResource(CommandHelper.createCollectionResource("CurrencyRate", getEntities()));

		return Result.SUCCESS;
	}

	List<OEntity> getEntities() {
		// get the currency
		String httpURL = "http://api.fixer.io/latest?base=GBP";
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(httpURL);
		List<OEntity> entities = new ArrayList<OEntity>();
		try {
			client.executeMethod(method);

			String jsonResponse = method.getResponseBodyAsString();

			// parse the currency
			JSONObject jo = new JSONObject(jsonResponse);
			String rate = jo.getString("rates");

			JSONObject keyVal = new JSONObject(rate);
			Iterator<?> it = keyVal.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = keyVal.getString(key);

				List<OProperty<?>> props = new ArrayList<OProperty<?>>();
				props.add(OProperties.string("CurrencyCode", key));
				props.add(OProperties.string("CurrencyValue", value));

				OEntityKey okey = OEntityKey.create(key);
				OEntity entity = OEntities.create(metadataOData4j.getEdmEntitySetByEntityName("CurrencyRate"), okey,
						props, new ArrayList<OLink>());
				entities.add(entity);
			}
		} catch (Exception e) {
			return null;
		}
		return entities;
	}
}
