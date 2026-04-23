# Creating a new release

From the `main` branch:

1. Run `git tag v<VERSION>` (e.g. `git tag v0.0.1`)
2. Run `git push origin v<VERSION>`
3. Because the tag name starts with `v`, the [release workflow](/.github/workflows/release.yml) should kick in
   automatically
   - Optionally, to monitor its progress, run `gh run watch`
