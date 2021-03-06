// Adds a user with username "a@dmin.se" and password "password" with full permissions.

db.users.insert({
  "_id" : ObjectId( "5519a6563004ba8bee704b62" ),
  "email" : "a@dmin.se",
  "firstName" : "Admin",
  "lastName" : "Admin",
  "hashedPassword" : "$shiro1$SHA-256$1$+0r1YFBHm551/AhX82PyRw==$tL6Nr6lTqnRCX7Tg4V6OMmNLESoLhdzkqW9OklXcKPk="
});

db.permissions.insert({
  "user" : { "id" : ObjectId( "5519a6563004ba8bee704b62" ) },
  "patterns" : [ "*" ]
});
