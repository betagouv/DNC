package fr.gouv.modernisation.dinum.dnc.franceconnect.factory;

import org.meanbean.lang.Factory;

import java.util.UUID;

public class UUIDFactory implements Factory<UUID> {

	@Override
	public UUID create() {
		return UUID.randomUUID();
	}
}
