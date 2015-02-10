# aside_main iTrust Version
This branch is the main version of aside customized for a study involving iTrust. The branch contains also contains a customized version of iTrust 1.9 needed to run the study.

Note, eclipse does not handle markers properly in iTrust files (the resolution generator is not called when a marker is clicked). To get around the problem. files used by the study were renamed to .java files. It should not be necessary to notify participants, but if they notice it can be explained.
Since eclipse does not handle jsp files very well, it may display many false errors in the file. To make them dismiss for the study, please run aside, wait for the new eclipse window to popup, right click the itrust project, left cick properties, choose the validation option, and disable validation for the project. Then click on one of the errors in the problems view and delete all errors. They should then dismiss and not bother the user during the study.

