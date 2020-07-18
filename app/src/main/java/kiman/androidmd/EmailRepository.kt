package kiman.androidmd

object EmailRepository {

  fun threads(): List<Email.EmailThread> {


    return listOf(
//            Email.EmailThread(
//                    id = 0,
//                    sender = Email.Person("MOTION 1", R.drawable.avatar_googleexpress),
//                    subject = "Contour Extractor",
//                    emails = listOf(
//                            Email(
//                                    excerpt = "2020-06-02 20:38:25",
//                                    body = "MOTION 1",
//                                    timestamp = "2020-06-02 20:38:25"))
//            ),
//
//            Email.EmailThread(
//                    id = 1,
//                    sender = Email.Person("MOTION 2", R.drawable.avatar_googleexpress),
//                    subject = "Google Crome",
//                    emails = listOf(
//                            Email(
//                                    excerpt = "2020-06-02 20:38:25",
//                                    body = "MOTION 2",
//                                    timestamp = "2020-06-02 20:38:25"))
//            ),
//
//            Email.EmailThread(
//                    id = 2,
//                    sender = Email.Person("MOTION 3", R.drawable.avatar_googleexpress),
//                    subject = "ABC",
//                    emails = listOf(
//                            Email(
//                                    body = "MOTOIN 3",
//                                    timestamp = "2020-06-02 20:38:25"))
//            ),
//
//            Email.EmailThread(
//                    id = 3,
//                    sender = Email.Person("MOTION 4", R.drawable.avatar_googleexpress),
//                    subject = "LOL",
//                    emails = listOf(
//                            Email(
//                                    body = "MOTION 4",
//                                    timestamp = "2020-06-02 20:38:25"))
//            ),
//            Email.EmailThread(
//                    id = 4,
//                    sender = Email.Person("MOTION 1", R.drawable.avatar_googleexpress),
//                    subject = "Contour Extractor",
//                    emails = listOf(
//                            Email(
//                                    excerpt = "2020-06-02 20:38:25",
//                                    body = "MOTION 1",
//                                    timestamp = "2020-06-02 20:38:25"))
//            ),
//
//            Email.EmailThread(
//                    id = 5,
//                    sender = Email.Person("MOTION 2", R.drawable.avatar_googleexpress),
//                    subject = "Google Crome",
//                    emails = listOf(
//                            Email(
//                                    excerpt = "2020-06-02 20:38:25",
//                                    body = "MOTION 2",
//                                    timestamp = "2020-06-02 20:38:25"))
//            ),
//
//            Email.EmailThread(
//                    id = 6,
//                    sender = Email.Person("MOTION 3", R.drawable.avatar_googleexpress),
//                    subject = "ABC",
//                    emails = listOf(
//                            Email(
//                                    body = "MOTOIN 3",
//                                    timestamp = "2020-06-02 20:38:25"))
//            ),
//
//            Email.EmailThread(
//                    id = 7,
//                    sender = Email.Person("MOTION 4", R.drawable.avatar_googleexpress),
//                    subject = "LOL",
//                    emails = listOf(
//                            Email(
//                                    body = "MOTION 4",
//                                    timestamp = "2020-06-02 20:38:25"))
//            )
    )
  }

  fun thread(id: EmailThreadId): Email.EmailThread {
    return threads().first { it.id == id }
//    val getlist : GetList =  GetList()
//    return getlist.getlist().first { it.id == id }
  }
}
