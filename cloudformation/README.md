## Q: Why do we have nested stacks?

A: Because CloudFormation is ridiculous.

We need to put the launch config into multiple security groups: one that we create in the main template, and a few more that are passed in as a parameter. 

Making a nested stack, with a `CommaDelimitedList` parameter, is the only way to append a string to a list of strings, generating another list of strings.

See http://stackoverflow.com/a/23559040/110856 for the gory details.
