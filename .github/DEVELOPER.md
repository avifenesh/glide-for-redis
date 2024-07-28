# CI/CD Workflow Guide

<span style="color: green;">
TODO: Add a description of the CI/CD workflow and its components.
</span>

### Overview

Our CI/CD pipeline tests and builds our project across multiple languages, versions, and environments. This guide outlines the key components and processes of our workflow.

### Workflow Triggers

-   Push to `main` branch
-   Pull requests
-   Scheduled runs (daily)
-   Manual trigger (workflow_dispatch)
-   Pull request with `Core changes` label

### Language-Specific Workflows

#### Python (python.yml)

#### Node.js (node.yml)

#### Java (java.yml)

Each language has its own workflow file with similar structure but language-specific steps.

### Shared Components

#### Run Field

The `run` field in both engine-matrix.json and build-matrix.json is used to specify when a particular configuration should be executed:

-   In engine-matrix.json:

    -   `"run": "always"`: The engine version will be tested in every workflow run.
    -   If `run` is omitted: The engine version will only be tested in full matrix runs (e.g., when the `Core changes` label is applied).

-   In build-matrix.json:
    -   The `run` array specifies which language workflows should use this host configuration.
    -   `"always"` in the array means the host will be used in every workflow run for the specified languages.

This allows for flexible control over which configurations are tested in different scenarios, optimizing CI/CD performance and resource usage.

#### Engine Matrix (engine-matrix.json)

Defines the versions of Valkey engine to test against:

```json
[
    { "type": "valkey", "version": "7.2.5", "run": "always" },
    { "type": "valkey", "version": "redis-7.0.15" },
    { "type": "valkey", "version": "redis-6.2.14", "run": "always" }
]
```

#### Build Matrix (build-matrix.json)

Defines the host environments for testing:

```json
[
    {
        "OS": "ubuntu",
        "RUNNER": "ubuntu-latest",
        "TARGET": "x86_64-unknown-linux-gnu",
        "run": ["always", "python", "node", "java"]
    }
    // ... other configurations
]
```

#### Adding New Versions or Hosts

To add a new engine version, update engine-matrix.json.
To add a new host environment, update build-matrix.json.

Ensure new entries have all required fields:

For engine versions: `type`, `version`, `run` (optional)
For hosts: `OS`, `RUNNER`, `TARGET`, `run`

#### Triggering Workflows

Push to main or create a pull request to run workflows automatically.
Add `Core changes` label to a pull request to trigger full matrix tests.
Use workflow_dispatch for manual triggers.

### Mutual vs. Language-Specific Components

#### Mutual

`Engine matrix`
`Build matrix`
`Shared dependencies installation`
`Linting (Rust)`

#### Language-Specific

`Package manager commands`
`Testing frameworks`
`Build processes`
`Version matrices`

### Customizing Workflows

Modify `[language].yml` files to adjust language-specific steps.
Update matrix files to change tested versions or environments.
Adjust cron schedules in workflow files for different timing of scheduled runs.