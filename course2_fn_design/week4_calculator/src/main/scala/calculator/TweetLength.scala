package calculator

object TweetLength {
  final val MaxTweetLength = 140

  def strLength(sequence: CharSequence): Int = {
    var count = 0
    var i = 0
    var skip = 0

    while ( i < sequence.length ) {
      val ch = sequence.charAt(i)
      if (ch <= 0x7F) count += 1
      else if (ch <= 0x7FF) count += 2
      else if (Character.isHighSurrogate(ch)) {
        count += 4
        i += 1
        skip += 1   // 2 combined unicode char count as 1 char.
      }
      else count += 3

      i += 1
    }

    // i is number of chars, count is number of bytes.
    i - skip
  }

  def tweetRemainingCharsCount(tweetText: Signal[String]): Signal[Int] = {
    Signal(MaxTweetLength - strLength(tweetText()))
  }

  def colorForRemainingCharsCount(remainingCharsCount: Signal[Int]): Signal[String] = {
    Signal(
      if(remainingCharsCount() >= 15)
        "green"
      else if(remainingCharsCount() >= 0)
        "orange"
      else
        "red"
    )
  }

  /** Computes the length of a tweet, given its text string.
   *  This is not equivalent to text.length, as tweet lengths count the number
   *  of Unicode *code points* in the string.
   *  Note that this is still a simplified view of the reality. Full details
   *  can be found at
   *  https://dev.twitter.com/overview/api/counting-characters
   */
  private def tweetLength(text: String): Int = {
    /* This should be simply text.codePointCount(0, text.length), but it
     * is not implemented in Scala.js 0.6.2.
     */
    if (text.isEmpty) 0
    else {
      text.length - text.init.zip(text.tail).count(
          (Character.isSurrogatePair _).tupled)
    }
  }
}
