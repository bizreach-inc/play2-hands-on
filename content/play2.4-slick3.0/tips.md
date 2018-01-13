---
title: Tips
---

## Play2

* playコマンド実行時のVMオプションの設定
  * ヒープ設定やプロキシ設定などJVMの起動オプションを設定するには環境変数`JAVA_OPTS`を使用します
* Playが使用するローカルリポジトリ
  * 以前のバージョンのPlayは`PLAY_HOME/repository`ディレクトリ配下に独自のローカルリポジトリとキャッシュを持っていましたが、Play 2.3ではSBT標準の`HOME/.ivy2`ディレクトリ配下を使うようになりました
* Play公式サイトのドキュメントは https://www.playframework.com/documentation/2.5.x/Home にあります（2.5.x向けのドキュメントは日本語には未翻訳です）
* Play 2.5以前から2.5へのマイグレーションについては以下のドキュメントを参照してください
  * [Play 2.5 Migration Guide](https://www.playframework.com/documentation/2.5.x/Migration25)

## Slick

* https://github.com/bizreach/slick-reference でSlickのリファレンスを公開しています
* Slick 2.xから3.0へのマイグレーションについては http://slick.typesafe.com/doc/3.0.0/upgrade.html#upgrade-from-2-1-to-3-0 を参照してください

## ScalaIDE

* Scala IDEはEclipseのバージョン、Scalaのバージョンにあわせて更新サイトが用意されています
  * 最新情報は http://scala-ide.org/download/current.html を参照してください

## IntelliJ IDEA

* キーボードショートカット
  * (Windows) http://www.jetbrains.com/idea/docs/IntelliJIDEA_ReferenceCard.pdf
  * (Mac) http://www.jetbrains.com/idea/docs/IntelliJIDEA_ReferenceCard_Mac.pdf
* IDEAの外観を変更
  * スキンの変更（背景を黒に）
    * [Appearance]→[Theme]の箇所を"Default"から"Darcula"に変更
* IntelliJ上でクラスパスが解決できない場合
  * StringなどJavaの基本的な型が解決できずエラーになってしまう場合は実行した直後はJDKが選択されていない可能性があります
    * [File]→[Project Structure...]からインストール済みのJDKを選択してください
![JDKを選択](../images/play2.4-slick3.0/idea_jdk.png)
  * `build.sbt`を変更してもクラスパスが解決されないことがありますが、その場合は「File」→「Invalidate Caches / Restart...」でIntelliJのキャッシュをクリアしてみてください

##その他

* Emacsユーザの方はScalaの開発環境としてEnsimeを利用できます
  * http://qiita.com/saito400/items/7ece94138ad76e091229
* vimユーザの方はScala開発環境としてEclim + ScalaIDEを利用できます
  * http://qiita.com/pecorarista/items/1aa7575aa40077d55b53
