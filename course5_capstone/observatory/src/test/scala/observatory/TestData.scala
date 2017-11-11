package observatory

object TestData {
  val manhattan = Location(40.7627608,  -73.9633166)    // central park, NY
  val flushing  = Location(40.7611869,  -73.8278084)    // Flushing NY
  val la        = Location(34.0201597, -118.6926134)    // LA
  val chicago   = Location(41.8333925,  -88.0121478)    // Chicago
  val sfo       = Location(37.7576792, -122.5078121)    // San Francisco
  val shanghai  = Location(31.6140163,  111.1240098)    // Shanghai
  val sahara    = Location(20.6989319,    7.9953434)    // sahara
  val mteverest = Location(27.9878493,   86.9162499)    // Mt. Everest
  val antarctica= Location(-75.966502,    3.601225)     // Antarctica

  val temperatures = Seq( (manhattan,    4.5),
                          (la,          30.0),
                          (sfo,         24.0),
                          (shanghai,    18.3),
                          (sahara,      49.0),
                          (mteverest,  -34.5),
                          (antarctica, -60.0)
                        )
}

