# Versioning
Androidplot's versioning scheme is Major.Minor.Rev.  For example, in the version 1.3.2 the Major is 1
the Minor is 3 and the Rev is 2.

Revisional releases (releases where only the Rev value has increased from the previous release, ie 1.3.1 -> 1.3.2) 
may include new features and bug fixes but will be fully backwards compatible with releases of the same minor version.
**Updating to the latest revisional release should require no code changes in your app.**

Minor Releases (releases where the Minor value has increased but the Major value has not) may contain
new features, bug fixes, removed methods deprecated in the previous minor release
and other moderate refactoring.  **Updating to a new minor release may require code changes in your app.**

Major releases (releases where the Major value has increased) are typically complete rewrites of core 
elements of the library.  Unless otherwise specified in the release notes, **updating to a new major release
will require significant code changes to your app.**