# Publishing Guide - Event Library v3.0.0

This guide provides step-by-step instructions for publishing Event Library v3.0.0 to Maven Central and GitHub.

---

## ✅ Pre-Publishing Checklist

- [x] All tests passing (63/63)
- [x] Version updated to 3.0.0 in pom.xml
- [x] CHANGELOG.md updated with v3.0.0 section
- [x] README.md updated with v3.0.0 examples
- [x] LICENSE file created (MIT)
- [x] RELEASE_NOTES.md created
- [x] All changes committed to git
- [x] Git tag v3.0.0 created
- [x] Code compiles without errors
- [x] Static analysis tools configured

---

## 📦 Publishing to GitHub

### Step 1: Push the Git Tag

```bash
cd /home/user/event
git push origin v3.0.0
```

### Step 2: Create GitHub Release

1. Go to: https://github.com/MasumNishat/event/releases/new
2. Select tag: `v3.0.0`
3. Release title: `v3.0.0 - Modern Java Event Library`
4. Copy the content from `RELEASE_NOTES.md` into the description
5. Check "Set as the latest release"
6. Click "Publish release"

---

## 🚀 Publishing to Maven Central

### Prerequisites

Before publishing to Maven Central, ensure you have:

1. **OSSRH Account**
   - Account at: https://issues.sonatype.org/
   - Configured in `~/.m2/settings.xml`

2. **GPG Key**
   - GPG key created and published to key server
   - Configured for Maven signing

3. **Maven Settings**

Create or update `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>YOUR_OSSRH_USERNAME</username>
      <password>YOUR_OSSRH_PASSWORD</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

### Publishing Steps

#### Option A: Manual Deploy (Recommended for first-time)

```bash
# 1. Clean and verify
mvn clean verify

# 2. Deploy to OSSRH
mvn deploy -Dgpg.skip=false

# 3. Check the staging repository at:
# https://s01.oss.sonatype.org/#stagingRepositories

# 4. Close and release the repository through the UI
```

#### Option B: Maven Release Plugin (Automated)

```bash
# 1. Prepare the release
mvn release:clean release:prepare

# 2. Perform the release
mvn release:perform

# 3. The plugin will:
#    - Update versions
#    - Create git tags
#    - Deploy to Maven Central
#    - Update to next SNAPSHOT version
```

### Verification

After publishing, verify the release:

1. **Maven Central Search**: https://search.maven.org/artifact/org.nishat.util/event
2. **Maven Repository**: https://repo1.maven.org/maven2/org/nishat/util/event/3.0.0/

It may take 15-30 minutes for the artifacts to appear on Maven Central.

---

## 📋 Post-Publishing Tasks

### 1. Announce the Release

**GitHub**
- Post announcement in repository discussions
- Update repository description if needed

**Social Media / Blog**
- Share release announcement
- Highlight key features

### 2. Update Documentation Sites

If you have external documentation:
- Update version references
- Add migration guides
- Update API documentation

### 3. Monitor Initial Feedback

- Watch GitHub issues for problems
- Monitor Maven Central download stats
- Check for user reports

---

## 🔄 Troubleshooting

### GPG Signing Issues

```bash
# List keys
gpg --list-keys

# Export public key to key server
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Test signing
mvn clean verify -Dgpg.skip=false
```

### Deploy Failures

**Issue**: 401 Unauthorized
**Solution**: Check OSSRH credentials in `~/.m2/settings.xml`

**Issue**: 403 Forbidden
**Solution**: Ensure you have rights to deploy to org.nishat.util group

**Issue**: GPG signing failed
**Solution**: Check GPG passphrase and key availability

### Staging Repository Issues

If the staging repository fails validation:
1. Log in to https://s01.oss.sonatype.org/
2. Check the "Activity" tab for error messages
3. Drop the staging repository
4. Fix issues and redeploy

---

## 📊 Version Management

### Current Version: 3.0.0

### Next Version Options:

**Patch Release (3.0.1)**
- Bug fixes only
- No breaking changes
- Backward compatible

**Minor Release (3.1.0)**
- New features
- No breaking changes
- Backward compatible

**Major Release (4.0.0)**
- Breaking changes
- Major new features
- Not backward compatible

### Updating for Next Release

```bash
# Update version in pom.xml
<version>3.0.1-SNAPSHOT</version>

# Update CHANGELOG.md
## [Unreleased]
### Added
- ...

## [3.0.1] - YYYY-MM-DD
### Fixed
- ...
```

---

## 🔐 Security Considerations

1. **Never commit credentials** to git
2. **Store GPG passphrase securely** (use gpg-agent)
3. **Use HTTPS** for Maven repository access
4. **Enable 2FA** on OSSRH account
5. **Regularly rotate** credentials

---

## 📞 Support

### Having Issues?

1. **Maven Central Guide**: https://central.sonatype.org/publish/
2. **OSSRH Guide**: https://central.sonatype.org/publish/publish-guide/
3. **GitHub Issues**: https://github.com/MasumNishat/event/issues

### Contact

- **Email**: masum.nishat21@gmail.com
- **GitHub**: https://github.com/MasumNishat

---

## 🎉 Success Checklist

After successful publishing, verify:

- [ ] Artifacts available on Maven Central
- [ ] GitHub release published with notes
- [ ] Git tag pushed to remote
- [ ] README badges updated (if any)
- [ ] Documentation sites updated
- [ ] Announcement posted
- [ ] Initial user feedback monitored

---

**Ready to publish?** Follow the steps above and your library will be available to millions of Java developers worldwide! 🚀
