Yarm Gwanga photoshare app readme and handover documentation - written by chris muir

Project Description:
the yarm gwanga photoshare app is a communication platform for teachers and parents at yarm gwanga intended to enablem the sharing, viewing and commenting on
photos of children at play with a photoboard in a similar style to instagram.
additionally it aims to facillitate communication with a direct messaging functionallity between teachers and parents. 
while other platforms and solutions for this purpose exist, our solution aims to minmise the amount of actions between taking a photo and posting it, as well as 
improve the ease of use for both teachers and parents over Yarm Gwangas existing solution. 

the project has been written in the kotlin language, and implements Google Firebase for image storage, and Mongo DB for all other database requirements


google Firebase:
i have created a firebase account to use for this project, all images are stored there and referenced by the url in order to be displayed in the app itsself.
Firebase offers a 3 month free trial with x amount of free credit to use within that time. it should be valid and available for another ~2 months at the time of writing.
changing this to an account whose purpouse is to host photos privately for yarm gwanga is reccomended for any team taking over the project.

mongoDB:
sam gould set up a mongoDB account to serve as a database handling user logins, and storing all data for custom user data, messages, photoboard posts and photoboard comments. 
my understanding is that the mongo DB realm should remain free and active permanantly but if it needs to be changed, contact sam for the relevant information. 

  User accounts:
  users are authenticated with an email:password combo. the information is stored on mongoDB and currently the only way to create a new valid account is with an existing teacher account. 
  there is currently no way to change passwords and the only time to add your password is during account creation meaning the teacher creating the account decides the password too. 
  it is probably a good idea to rectify this, adding more options for account creation, or potentially a good id3ea to rebrand the "password" as an "access code"
  
  user custom data:
  mongodb supports custom user data linked to a users account. the current implementation has fields for the linking user id, the partition required by mongoDB, 
  the accounts shortname, the account type (parent/teacher) the childrens name ( not actually used for anything currently, only supports one child in urrent implementation)
  an access code defining which classroom/s the parent has access to(1, 2, 4, 8 or a combination denoting access to multiple) and an "active" variable, denoting whether the account is valid or not.
  active is a holdover from bad paradigs with account creation but at the current time its critical and cant be removed untill account creation is reworked. 
  
  messages schema:
  messages objects contain values  for _id (required by mongo db, unique identifier), _partition (required by mongo db, controlls access?), sender (unique user id of the account that sent the message),
  senderSname (short name of message sender), time (date and time that message was sent), conversation (unique user id of the parent in the relevant conversation, used to determine which conversation messages belong to),
  and message and image (messages contain either one, not both, either the mesage contents or the image url to be shown). 
  messages have a 1 to many format, where each parent converses with all the teachers at the same time, all teachers have access to the same chat with each individual parent. 
  
  photopost schema:
  the fields are the same as the schema for messages with the ecxception of conversation, which is replaced with "room", an int denoting which room the post belongs to
  
  photocomment schema:
  again the same  schema as messages, but replacing conversation with post, which is a combination of the posters shortname and the datre and time it was created. 
  

file breakdown:
  mainActivity->controlls the login functionality
  mainActivity2-> a home page of sorts for teachers allowing to chose which room or chat they wish to enter
  messages, messagesItem, messageRecyclerAdapter -> all relavant files for messages, message items, and displayiong messages within the messages recyclerView
  photoboardActivity, photoPostItem, photoboardRecyclerAdapterLeftie, photoboardRecyclerAdapterRightie -> all relevant code forthe photoboards. photoboards feature 2 recyclerviews
    and as such requires a left and right adapter. 
  photopostActivity-> activity responsable for allowing a user to make posts to photoboard, creates photoPost items
  photoCommentActivity, PhotoCommentRecyclerAdapter, PhotocommentItem ->all code responsible for displaying photoboard posts wioth comments
  cameraActivity -> code handling camera for taking pictures, launched from messages and photoposts
  accountManagementActivity, createAccountFragment, editAccountFragment->code responsible for creating and managing accounts. creat accounts works fine but edit accounts in non functional at the time of writing.
  udps.kt -> realm app init file? not 100% sure what this does but i know its needed. 
  headerFragment, messagesinput, photoboardCommentsActivity, scrollingFragmentMessages -> depreciated code im too scared to remove
  message_sent_cardview, message_recieved_cardview, message_recieved2_cardview, photo_comment_cardview, photoboard_image_card -> layouts for recyclerviews
  
  
photoboard:
photoboards are set up by pulling all photopost items with the relevant access code then displaying them in the two recycler views


direct messaging:
direct messaging functions by pulling all messages with the relevant conversation id and displays them in the recycler view. input is available to take photos or chose images from gallery, or to enter text to create a message item.


known issues: 
>edit accounts is non functional, account set up is entirely done by teachers, may be problematic in the future
>name doesnt display properly in messages under certain situuations
>photoboard recyclerviews are slightly offset and dont scroll together perfectly, rarely cause crashes on init
>pulling everything from server is slow, its not conveyed very well that something is happening during the load process. loads occour every time you go to an activity.
    i do not have a proper solution for this at this time
>something else probably



