sign4j version 3.0
------------------

sign4j is a very simple utility to digitally sign executables containing an appended jar file, like those created by launch4j.

It works by first signing a temporary file to detect the size of the applied signature, and by then adding that size to a counter in the ZIP_END_HEADER of the embedded jar, so as to pretend that the signature is a comment belonging to it. That way the jar remains formally correct, and java accepts it.

This manipulation must be done atomically with the signing process, because doing it before would invalidate the jar file, while doing it later would break the signature. That's why the whole command line of your signing tool must be passed to sign4j, which will do the job.

Any signing tool can be used, as long as the name of the output file can be recognized among its parameters. This is currently done by either using an -out option if present, or taking the last filename with an exe suffix after all supplied options.

If the involved tool is able to remove a previous signature before adding the new one (as is normally the case) the initial test can be performed on the target executable itself, avoiding the creation of a temporary file. You can use the option --onthespot to signal that to sign4j.

The option --strict can be used to suppress the use of double quotes around parameters that strictly don't need them. The option --verbose shows diagnostics about intermediary steps of the process.

This utility can also be used to sign normal executables, but then it will remember you that the file can be signed directly.

Please send comments to bramfeld@diogen.de
