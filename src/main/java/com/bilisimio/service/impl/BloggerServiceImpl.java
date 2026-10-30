package com.bilisimio.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bilisimio.dto.UpdateBloggerInput;
import com.bilisimio.entity.Address;
import com.bilisimio.entity.Blogger;
import com.bilisimio.enums.AddressType;
import com.bilisimio.enums.GenderType;
import com.bilisimio.exception.InvalidRequestException;
import com.bilisimio.exception.ResourceNotFoundException;
import com.bilisimio.service.BloggerService;

@Service
public class BloggerServiceImpl implements BloggerService {

	private final Map<Long, Blogger> bloggers = new ConcurrentSkipListMap<>();
	private final AtomicLong sequence = new AtomicLong();

	public BloggerServiceImpl() {
		seed("Alper", "AKALIN", "alper@bilisim.io");
		seed("Kubilay", "KULAOĞLU", "kubilay@bilisim.io");
		seed("Ömer", "KOÇBİL", "omer@bilisim.io");
		seed("Barış", "UYAR", "baris@bilisim.io");
		seed("Burak", "KARADAĞ", "burak@bilisim.io");
		seed("Tahir", "KARDAK", "tahir@bilisim.io");
		seed("Volkan", "ÖZDEMİR", "volkan@bilisim.io");
		seed("Cem", "DIRMAN", "cem@bilisim.io");
	}

	private void seed(String name, String surname, String email) {
		Blogger blogger = create(new Blogger(null, name, surname, email, "password"));
		blogger.setGender(GenderType.MALE);
		blogger.setAddress(new Address(blogger.getId(), AddressType.OFFICE, "İstanbul"));
	}

	@Override
	public List<Blogger> findAll() {
		return new ArrayList<>(bloggers.values());
	}

	@Override
	public Optional<Blogger> findById(Long id) {
		return Optional.ofNullable(bloggers.get(id));
	}

	@Override
	public Blogger getById(Long id) {
		return findById(id).orElseThrow(() -> new ResourceNotFoundException("Blogger", id));
	}

	@Override
	public Map<Long, Blogger> findAllByIds(Collection<Long> ids) {
		return ids.stream().distinct().filter(bloggers::containsKey)
				.collect(Collectors.toMap(id -> id, bloggers::get));
	}

	@Override
	public synchronized Blogger create(Blogger blogger) {
		if (blogger.getId() == null) {
			blogger.setId(sequence.incrementAndGet());
		} else if (bloggers.containsKey(blogger.getId())) {
			throw new InvalidRequestException("Bu id ile bir blogger zaten mevcut: " + blogger.getId());
		} else {
			sequence.accumulateAndGet(blogger.getId(), Math::max);
		}
		assertEmailAvailable(blogger.getEmail(), blogger.getId());
		bloggers.put(blogger.getId(), blogger);
		return blogger;
	}

	@Override
	public synchronized Blogger update(UpdateBloggerInput input) {
		Blogger blogger = getById(input.id());
		if (input.email() != null) {
			assertEmailAvailable(input.email(), blogger.getId());
			blogger.setEmail(input.email());
		}
		if (input.name() != null) {
			blogger.setName(input.name());
		}
		if (input.surname() != null) {
			blogger.setSurname(input.surname());
		}
		if (input.gender() != null) {
			blogger.setGender(input.gender());
		}
		if (input.address() != null) {
			blogger.setAddress(new Address(blogger.getId(), input.address().addressType(), input.address().addressField()));
		}
		return blogger;
	}

	@Override
	public boolean delete(Long id) {
		if (bloggers.remove(id) == null) {
			throw new ResourceNotFoundException("Blogger", id);
		}
		return true;
	}

	private void assertEmailAvailable(String email, Long ownerId) {
		if (email == null) {
			return;
		}
		boolean taken = bloggers.values().stream()
				.anyMatch(b -> email.equalsIgnoreCase(b.getEmail()) && !b.getId().equals(ownerId));
		if (taken) {
			throw new InvalidRequestException("E-posta zaten kullanımda: " + email);
		}
	}

}
