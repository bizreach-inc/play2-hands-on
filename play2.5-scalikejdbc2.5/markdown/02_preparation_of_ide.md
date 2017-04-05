# 02.IDEの準備

## プラグインのインストール

Java7以降 + IntelliJは予めインストール済みとします。まずは以下の手順でIntelliJにScala開発用のプラグインを導入します。

* メニュー[IntelliJ IDEA]→[Preferences]→[Plugins]→[Install JetBrains plugin...]をクリック
* [Scala]を選択し、右クリック[Download and Install]をクリック

※IntelliJ Ultimate EditionはPlayプラグインを使うことができ、PlayプロジェクトをIntelliJで作成したり、HTMLテンプレートや設定ファイルなどを編集するためのエディタが追加されます。

## プロジェクトのインポート

IntelliJのScalaプラグインはSBTプロジェクトをネイティブサポートしており、「File」メニューから「Open」を選択し、Play2プロジェクト内の`build.sbt`を選択するとSBTプロジェクトとしてインポートすることができます（プロジェクトのルートディレクトリを選択するとGradleプロジェクトとしてインポートされてしまいますので注意してください）。

![プロジェクトのインポート(1)](images/open_project_intellij1.png)

インポートする際に以下のダイアログが表示されます。初回は「Project SDK」が未選択の状態になっているかもしれません。「New...」をクリックしてJDKがインストールされているディレクトリを選択してから「OK」をクリックしてください。

![プロジェクトのインポート(2)](images/open_project_intellij2.png)

`build.sbt`を編集してライブラリを追加した場合、ウィンドウ右上に以下のようなメッセージが表示されます。

![プロジェクトのリフレッシュ](images/re-import_project.png)

「Refresh」を選択するとプロジェクトが再インポートされ、ライブラリが自動的にインターネット経由でダウンロードされクラスパスに追加されます。また、「Enable auto-import」を選択するとbuild.sbtを変更するたびに自動的に再インポートされるようになります（プロジェクトのインポート時に自動インポートを有効にしておくことも可能です）。

----
[＜プロジェクトの作成に戻る](01_create_project.md) | [DBの準備に進む＞](03_preparation_of_db.md)
