package cn.watchdog.dependencies;

import cn.watchdog.dependencies.relocation.Relocation;
import cn.watchdog.dependencies.relocation.RelocationHelper;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public enum Dependency {
	CONFIGURATE_CORE(
			"org{}spongepowered",
			"configurate-core",
			"3.7.2",
			"XF2LzWLkSV0wyQRDt33I+gDlf3t2WzxH1h8JCZZgPp4=",
			Relocation.of("configurate", "ninja{}leaping{}configurate")
	),
	CONFIGURATE_GSON(
			"org{}spongepowered",
			"configurate-gson",
			"3.7.2",
			"9S/mp3Ig9De7NNd6+2kX+L4R90bHnAosSNVbFjrl7sM=",
			Relocation.of("configurate", "ninja{}leaping{}configurate")
	),
	CONFIGURATE_YAML(
			"org{}spongepowered",
			"configurate-yaml",
			"3.7.2",
			"OBfYn4nSMGZfVf2DoZhZq+G9TF1mODX/C5OOz/mkPmc=",
			Relocation.of("configurate", "ninja{}leaping{}configurate")
	),
	SNAKEYAML(
			"org.yaml",
			"snakeyaml",
			"1.28",
			"NURqFCFDXUXkxqwN47U3hSfVzCRGwHGD4kRHcwzh//o=",
			Relocation.of("yaml", "org{}yaml{}snakeyaml")
	),
	MONGODB_DRIVER_CORE(
			"org.mongodb",
			"mongodb-driver-core",
			"4.5.0",
			"awqoW0ImUcrCTA2d1rDCjDLEjLMCrOjKWIcC7E+zLGA=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	MONGODB_DRIVER_LEGACY(
			"org.mongodb",
			"mongodb-driver-legacy",
			"4.5.0",
			"77KZGIr3KZmzBpN69rGOLXmnlJIBCXRl/U4gEIdlFhY=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	MONGODB_DRIVER_SYNC(
			"org.mongodb",
			"mongodb-driver-sync",
			"4.5.0",
			"q9XDSGJjlo/Ek6jHoCbqWnaK/dghB8y9aDM0hCLiSvk=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	MONGODB_DRIVER_BSON(
			"org.mongodb",
			"bson",
			"4.5.0",
			"6CFyEzxbdeiBEXdDBmcgqWs5dvicgFkBLU3MlQUIqRA=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	SLF4J_SIMPLE(
			"org.slf4j",
			"slf4j-simple",
			"1.7.30",
			"i5J5y/9rn4hZTvrjzwIDm2mVAw7sAj7UOSh0jEFnD+4="
	),
	SLF4J_API(
			"org.slf4j",
			"slf4j-api",
			"1.7.30",
			"zboHlk0btAoHYUhcax6ML4/Z6x0ZxTkorA1/lRAQXFc="
	),
	HIKARI(
			"com{}zaxxer",
			"HikariCP",
			"4.0.3",
			"fAJK7/HBBjV210RTUT+d5kR9jmJNF/jifzCi6XaIxsk=",
			Relocation.of("hikari", "com{}zaxxer{}hikari")
	),
	MARIADB_DRIVER(
			"org{}mariadb{}jdbc",
			"mariadb-java-client",
			"3.1.3",
			"ESl+5lYkJsScgTh8hgFTy8ExxMPQQkktT20tl6s6HKU=",
			Relocation.of("mariadb", "org{}mariadb{}jdbc")
	),
	MYSQL_DRIVER(
			"mysql",
			"mysql-connector-java",
			"8.0.23",
			"/31bQCr9OcEnh0cVBaM6MEEDsjjsG3pE6JNtMynadTU=",
			Relocation.of("mysql", "com{}mysql")
	),
	POSTGRESQL_DRIVER(
			"org{}postgresql",
			"postgresql",
			"42.6.0",
			"uBfGekDJQkn9WdTmhuMyftDT0/rkJrINoPHnVlLPxGE=",
			Relocation.of("postgresql", "org{}postgresql")
	),
	H2_DRIVER_LEGACY(
			"com.h2database",
			"h2",
			// seems to be a compat bug in 1.4.200 with older dbs
			// see: https://github.com/h2database/h2database/issues/2078
			"1.4.199",
			"MSWhZ0O8a0z7thq7p4MgPx+2gjCqD9yXiY95b5ml1C4="
			// we don't apply relocations to h2 - it gets loaded via
			// an isolated classloader
	),
	H2_DRIVER(
			"com.h2database",
			"h2",
			"2.1.214",
			"1iPNwPYdIYz1SajQnxw5H/kQlhFrIuJHVHX85PvnK9A="
			// we don't apply relocations to h2 - it gets loaded via
			// an isolated classloader
	),
	SQLITE_DRIVER(
			"org.xerial",
			"sqlite-jdbc",
			"3.28.0",
			"k3hOVtv1RiXgbJks+D9w6cG93Vxq0dPwEwjIex2WG2A="
			// we don't apply relocations to sqlite - it gets loaded via
			// an isolated classloader
	),
	COMMONS_POOL_2(
			"org.apache.commons",
			"commons-pool2",
			"2.9.0",
			"vJGbQmv6+zHsxF1mUqnxN0YkZdhJ+zhz142Qw/jTWwE=",
			Relocation.of("commonspool2", "org{}apache{}commons{}pool2")
	),
	JEDIS(
			"redis.clients",
			"jedis",
			"4.4.3",
			"wwwoCDPCywcfoNwpvwP95kXYusXSTtXhuVrB31sxE0k=",
			Relocation.of("jedis", "redis{}clients{}jedis"),
			Relocation.of("commonspool2", "org{}apache{}commons{}pool2")
	),
	CAFFEINE(
			"com{}github{}ben-manes{}caffeine",
			"caffeine",
			"2.9.0",
			"VFMotEO3XLbTHfRKfL3m36GlN72E/dzRFH9B5BJiX2o=",
			Relocation.of("caffeine", "com{}github{}benmanes{}caffeine")
	),
	OKIO(
			"com{}squareup{}" + RelocationHelper.OKIO_STRING,
			RelocationHelper.OKIO_STRING,
			"1.17.5",
			"Gaf/SNhtPPRJf38lD78pX0MME6Uo3Vt7ID+CGAK4hq0=",
			Relocation.of(RelocationHelper.OKIO_STRING, RelocationHelper.OKIO_STRING)
	),
	OKHTTP(
			"com{}squareup{}" + RelocationHelper.OKHTTP3_STRING,
			"okhttp",
			"3.14.9",
			"JXD6tVUVy/iB16TO70n8UVSQvAJwV+Zmd2ooMkZa7KA=",
			Relocation.of(RelocationHelper.OKHTTP3_STRING, RelocationHelper.OKHTTP3_STRING),
			Relocation.of(RelocationHelper.OKIO_STRING, RelocationHelper.OKIO_STRING)
	),
	;

	private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";
	private final String mavenRepoPath;
	private final String version;
	@Getter
	private final byte[] checksum;
	@Getter
	private final List<Relocation> relocations;

	Dependency(String groupId, String artifactId, String version, String checksum) {
		this(groupId, artifactId, version, checksum, new Relocation[0]);
	}

	Dependency(String groupId, String artifactId, String version, String checksum, Relocation... relocations) {
		this.mavenRepoPath = String.format(MAVEN_FORMAT,
				rewriteEscaping(groupId).replace(".", "/"),
				rewriteEscaping(artifactId),
				version,
				rewriteEscaping(artifactId),
				version);
		this.version = version;
		this.checksum = Base64.getDecoder().decode(checksum);
		this.relocations = ImmutableList.copyOf(relocations);
	}

	private static String rewriteEscaping(String s) {
		return s.replace("{}", ".");
	}

	/**
	 * Creates a {@link java.security.MessageDigest} suitable for computing the checksums
	 * of dependencies.
	 *
	 * @return the digest
	 */
	public static MessageDigest createDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String getFileName(String classifier) {
		String name = name().toLowerCase(Locale.ROOT).replace('_', '-');
		String extra = classifier == null || classifier.isEmpty() ? "" : "-" + classifier;

		return name + "-" + this.version + extra + ".jar";
	}

	String getMavenRepoPath() {
		return this.mavenRepoPath;
	}

	public boolean checksumMatches(byte[] hash) {
		return Arrays.equals(this.checksum, hash);
	}
}
