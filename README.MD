## example usage for github action on android

### for reference:[githubAction for android](https://github.com/wajahatkarim3/Android-Github-Actions/tree/main)
    
### actions/create-release@v1 已不再维护，改为使用 [action-gh-release](https://github.com/softprops/action-gh-release)

## [create-android-release-using-github-actions](https://proandroiddev.com/create-android-release-using-github-actions-c052006f6b0b)

- 往master分支push, commit message 包含 "[Build]" 触发assembleDebug

eg: 

```
git commit -m "feature [Build] xxxxx "
```

- 往release分支push, 触发assembleRelease

eg: 

```
git switch release
git merge master
git push
```

- 创建tag(v_开头的)并push,触发assembleRelease，并发布githubRelease

eg:

```
git tag v_1.0.3 -a -m "[v1.0.3] something" ## 创建一个叫做v_1.0.3的tag
git push origin --tags ## 将所有tag push到远端
git tag -d v_1.0.3 ## in case you may want to delete this tag
```



[practical examples for using github actions and docker](https://github.com/userdocs/btop-crossbuilds/blob/main/.github/workflows/matrix-btop-make-and-release.yml)

[more cron examples](https://github.com/userdocs/qbt-musl-cross-make/tree/main/.github/workflows)