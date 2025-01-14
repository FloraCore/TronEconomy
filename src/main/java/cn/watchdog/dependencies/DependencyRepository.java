package cn.watchdog.dependencies;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Represents a repository which contains {@link Dependency}s.
 */
public enum DependencyRepository {

	/**
	 * Maven Central mirror repository.
	 *
	 * <p>This is used to reduce the load on repo.maven.org.</p>
	 *
	 * <p>Although Maven Central is technically a CDN, it is meant for developer use,
	 * not end-user products. It is trivial and not very expensive for us to provide a
	 * mirror, which will absorb any traffic caused by LP.</p>
	 *
	 * <p>LuckPerms will fallback to the real-thing if the mirror ever goes offline.
	 * Retrieved content is validated with a checksum, so there is no risk to integrity.</p>
	 */
	MAVEN_CENTRAL_MIRROR_1("https://libraries.luckperms.net/") {
		@Override
		protected URLConnection openConnection(Dependency dependency) throws IOException {
			URLConnection connection = super.openConnection(dependency);
			connection.setRequestProperty("User-Agent", "FloraCore");

			// Set a connect/read timeout, so if the mirror goes offline we can fall back
			// to Maven Central within a reasonable time.
			connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
			connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

			return connection;
		}
	},
	/**
	 * 华为云的Maven镜像。
	 */
	MAVEN_CENTRAL_MIRROR_2("https://repo.huaweicloud.com/repository/maven/") {
		@Override
		protected URLConnection openConnection(Dependency dependency) throws IOException {
			URLConnection connection = super.openConnection(dependency);
			connection.setRequestProperty("User-Agent", "FloraCore");

			// Set a connect/read timeout, so if the mirror goes offline we can fall back
			// to Maven Central within a reasonable time.
			connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
			connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

			return connection;
		}
	},
	/**
	 * Maven Central.
	 */
	MAVEN_CENTRAL("https://repo1.maven.org/maven2/"),
	JIT_PACK("https://jitpack.io/"),
	PAPER_MC("https://papermc.io/repo/repository/maven-public/");

	private final String url;

	DependencyRepository(String url) {
		this.url = url;
	}

	/**
	 * Downloads the {@code dependency} to the {@code file}, ensuring the
	 * downloaded bytes match the checksum.
	 *
	 * @param dependency the dependency to download
	 * @param file       the file to write to
	 * @throws DependencyDownloadException if unable to download
	 */
	public void download(Dependency dependency, Path file) throws DependencyDownloadException {
		try {
			Files.write(file, download(dependency));
		} catch (IOException e) {
			throw new DependencyDownloadException(e);
		}
	}

	/**
	 * Downloads the raw bytes of the {@code dependency}, and ensures the downloaded
	 * bytes match the checksum.
	 *
	 * @param dependency the dependency to download
	 * @return the downloaded bytes
	 * @throws DependencyDownloadException if unable to download
	 */
	public byte[] download(Dependency dependency) throws DependencyDownloadException {
		byte[] bytes = downloadRaw(dependency);

		// compute a hash for the downloaded file
		byte[] hash = Dependency.createDigest().digest(bytes);

		// ensure the hash matches the expected checksum
		if (!dependency.checksumMatches(hash)) {
			throw new DependencyDownloadException("Downloaded file had an invalid hash. " + "Expected: " + Base64.getEncoder()
					.encodeToString(
							dependency.getChecksum()) + " " + "Actual: " + Base64.getEncoder()
					.encodeToString(
							hash));
		}

		return bytes;
	}

	/**
	 * Downloads the raw bytes of the {@code dependency}.
	 *
	 * @param dependency the dependency to download
	 * @return the downloaded bytes
	 * @throws DependencyDownloadException if unable to download
	 */
	public byte[] downloadRaw(Dependency dependency) throws DependencyDownloadException {
		try {
			URLConnection connection = openConnection(dependency);
			try (InputStream in = connection.getInputStream()) {
				byte[] bytes = ByteStreams.toByteArray(in);
				if (bytes.length == 0) {
					throw new DependencyDownloadException("Empty stream");
				}
				return bytes;
			}
		} catch (Exception e) {
			throw new DependencyDownloadException(e);
		}
	}

	/**
	 * Opens a connection to the given {@code dependency}.
	 *
	 * @param dependency the dependency to download
	 * @return the connection
	 * @throws java.io.IOException if unable to open a connection
	 */
	protected URLConnection openConnection(Dependency dependency) throws IOException {
		URL dependencyUrl = new URL(this.url + dependency.getMavenRepoPath());
		return dependencyUrl.openConnection();
	}

}
