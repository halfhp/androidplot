# Releasing Androidplot

Releases are driven by git tags.  Pushing a tag named after the version runs the
[release workflow](../.github/workflows/release.yml), which does everything else.  This doc covers
what to check before tagging, what the workflow does, how to verify the result, and what to do
afterwards.

## Versioning

* `theVersionName` in the root `build.gradle` is the single source of truth for the library and demo
  app version (e.g. `1.6.0`).  See [versioning](versioning.md) for what a version number promises.
* The demo app's version code is derived from the git commit count, so it never needs editing.
* Every push to `master` publishes `<theVersionName>-SNAPSHOT` of the library to the
  [Central snapshots repository](https://central.sonatype.com/repository/maven-snapshots/) and a
  beta of the demo app to the Play Store's open testing track.  Between releases, `theVersionName`
  should therefore already be the *next* version, so snapshots preview what the next release will be.

## Before tagging

Open one "Prepare the X.Y.Z release" pull request that does the following, and merge it.

1. Confirm `theVersionName` in `build.gradle` is the version being released.  The workflow refuses a
   tag that does not match it exactly.
2. Finish the `# X.Y.Z` section at the top of [release_notes.md](release_notes.md).  Everything
   between that heading and the next `# ` heading becomes the body of the GitHub release, so it
   should read well on its own: a short summary first, then the full list of changes, with a
   **Behavior changes** subsection if anything observable changed.
3. Point the dependency snippets at the new version: the `implementation
   "com.androidplot:androidplot-core:..."` lines in [README.md](../README.md) and
   [quickstart.md](quickstart.md).
4. Check that the last `master` build is green and that its snapshot and beta were published (the
   "Publish Snapshot to Maven Central" and "Publish Demoapp to Google Play (beta)" steps of the
   build workflow).  The release is built from the same code, so this is the dry run.
5. If the release includes behavior changes, skim the docs for statements they invalidate.

## Tagging

Tags are the bare version number, matching the existing releases (`1.5.11`, not `v1.5.11`):

```
git checkout master && git pull
git tag 1.6.0
git push origin 1.6.0
```

Pushing the tag is the irreversible step.  Anyone with write access to the repository can do it,
so treat tag pushes with the same care as a production deploy.

## What the workflow does

In order, stopping at the first failure:

1. Verifies the tag equals `theVersionName`.
2. Runs the unit tests and builds the release AAR.
3. Builds the signed demo app APK (`-Prelease`, which gives it the release version name and an odd
   version code one above the beta of the same commit).
4. Creates the GitHub release named after the tag, with the release-notes section as its body and
   the AAR and APK attached.
5. Publishes the library to Maven Central via the Central Publisher Portal and asks the Portal to
   validate and release it automatically.
6. Uploads the demo app APK to the Play Store's production track.

Watch it under **Actions → release androidplot**.

## Verifying

* **GitHub**: the release appears under Releases with the notes and two assets.
* **Maven Central**: the deployment shows as *Published* at
  [central.sonatype.com/publishing/deployments](https://central.sonatype.com/publishing/deployments)
  within a few minutes; the artifact becomes resolvable shortly after and searchable within about
  half an hour.  A quick check once it is live:
  ```
  curl -sI https://repo1.maven.org/maven2/com/androidplot/androidplot-core/1.6.0/androidplot-core-1.6.0.pom | head -1
  ```
* **Play Store**: the new version is listed under Production in the Play Console, pending review.

## If something fails

The steps run in the order above, so a failure leaves everything before it done.  Fix the cause,
then re-run only what is missing with **Actions → release androidplot → Run workflow** on the tag,
unchecking the steps that already succeeded.  Do not delete and re-push the tag: Maven Central
does not allow a released version to be replaced, and Play will not accept a version code it has
already seen.

If a release must be withdrawn, cut a new patch version rather than editing the old one.

## After the release

Open a small follow-up pull request that:

1. Bumps `theVersionName` in `build.gradle` to the next version (e.g. `1.6.1`), so `master`
   snapshots stop shadowing the released version.
2. Adds an empty `# 1.6.1` section at the top of `release_notes.md` for changes to accumulate under.
3. Points the snapshot example in [quickstart.md](quickstart.md) at `1.6.1-SNAPSHOT`.

The dependency snippets keep the released version until the next release.

## Credentials the workflow uses

All are repository secrets; none live in the repo.

| Secret | Used for |
|---|---|
| `DEMOAPP_KEYSTORE`, `DEMOAPP_KEYSTORE_PASSWORD`, `DEMOAPP_KEY_PASSWORD` | Signing the demo app (base64 keystore; alias `Key0`) |
| `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_PASSWORD` | A Central Publisher Portal user token |
| `GPG_SIGNING_KEY`, `GPG_SIGNING_PASSWORD` | Signing the Maven artifacts |
| `PLAY_PUBLISHER_JSON` | Play Console service account key |

Rotating any of them is a matter of replacing the secret; the workflow reads them fresh each run.
