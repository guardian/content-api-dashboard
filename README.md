# Content API Dashboard

This is the entry point for anyone (CAPI team or otherwise) who wants to do any ops work on CAPI.

It's basically a collection of links to other dashboards and useful places, plus a cheatsheet of commands to copy-paste.

## To run locally

```
$ sbt run
```

## To run the tests

Don't need no tests. It compiles!

## To deploy

When you push to master (or merge a PR), TeamCity will build the app and RiffRaff will automatically deploy it.

## Configuration

All the details about the links to display are read from the Play config. In production, the config file is downloaded from an S3 bucket when the machine starts up.
