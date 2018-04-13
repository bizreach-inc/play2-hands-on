---
title: IDEの準備
---

## プラグインのインストール

Java8 + IntelliJは予めインストール済みとします。まずは以下の手順でIntelliJにScala開発用のプラグインを導入します。

* メニュー[IntelliJ IDEA]→[Preferences]→[Plugins]→[Install JetBrains plugin...]をクリック
* [Scala]を選択し、右クリック[Download and Install]をクリック

※IntelliJ Ultimate EditionはPlayプラグインを使うことができ、PlayプロジェクトをIntelliJで作成したり、HTMLテンプレートや設定ファイルなどを編集するためのエディタが追加されます。

## プロジェクトのインポート

IntelliJのScalaプラグインはSBTプロジェクトをネイティブサポートしており、「Import Project」をクリックし、Play2プロジェクトのルートディレクトリを選択するとSBTプロジェクトとしてインポートすることができます。

![プロジェクトのインポート(1)](../images/play2.6-scalikejdbc3.2/open_project_intellij1.png)

![プロジェクトのインポート(2)](../images/play2.6-scalikejdbc3.2/open_project_intellij2.png)

インポートする際に以下のダイアログが表示されます。初回は「Project JDK」が未選択の状態になっているかもしれません。「New...」をクリックしてJDKがインストールされているディレクトリを選択してから「OK」をクリックしてください。

![プロジェクトのインポート(3)](../images/play2.6-scalikejdbc3.2/open_project_intellij3.png)

`build.sbt`を編集してライブラリを追加した場合、ウィンドウ右上に以下のようなメッセージが表示されます。

![プロジェクトのリフレッシュ](../images/play2.6-scalikejdbc3.2/re-import_project.png)

「Refresh」を選択するとプロジェクトが再インポートされ、ライブラリが自動的にインターネット経由でダウンロードされクラスパスに追加されます。また、「Enable auto-import」を選択するとbuild.sbtを変更するたびに自動的に再インポートされるようになります。
