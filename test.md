## 背景说明
feedbase3.0的job调度会由Gear调用shell来实现，为统一shell风格，约定feedbase3.0规范

----------------------

# feedbase3.0规范
## 使用场景
1. 脚本必须以`#!/bin/bash`开始，指定必须使用bash,防止机器配置不一致调用不同的shell
2. Shell只能用于简单工具或者其它工具的简单封装
    + Gear调用
    大部分脚本是全量任务,供Gear调用
    + 打包和部署
    包括build,make,deploy等,在feedbase的根目录下
## 命名规范
1. 后缀
    可执行脚本，不要有扩展名或者扩展名为`.sh`；公用库文件必须带扩展名.sh，并且`不可执行`
    feedbase3.0中定义脚本权限的位置：recinfra-feedbase/src/main/package/feedbase.qicf
2. 文件
    小写，下划线分隔，例如join_circle_feed_video_handler
3. 函数名
    小写，下划线分隔，不允许使用包分隔符::，定义函数必须加上`function`关键字，`()`不允许省略
4. 变量名
    小写，下划线分隔
5. 常量和环境变量
    大写，下划线分隔，定义在文件开头
6. 只读变量
    使用`readonly`显示声明只读变量，防止被误修改
7. 本地变量
    使用`local`声明函数范围变量，如果变量赋值是调用命令执行，必须将local和执行命令分开定义
    https://stackoverflow.com/questions/4421257/why-does-local-sweep-the-return-code-of-a-command
8. 函数位置
    函数定义放在常量后面，函数之前只允许有`include、set和常量定义`
    函数之间不允许有可执行代码
9. 定义main函数
    main函数放在文件最后面作为统一入口，方便快速定位脚本执行入口，在最后一行以`main "$@"`调用
    仅在shell较长、方法比较多时使用
## 命令调用
1. 检查返回值
    + 对于不使用管道的命令直接使用if或者$?判断返回结果
    ```bash
    if ! mv "${file_list}" "${dest_dir}/" ; then
      echo "Unable to move ${file_list} to ${dest_dir}" >&2
      exit "${E_BAD_MOVE}"
    fi
    # Or
    mv "${file_list}" "${dest_dir}/"
    if [[ "$?" -ne 0 ]]; then
      echo "Unable to move ${file_list} to ${dest_dir}" >&2
      exit "${E_BAD_MOVE}"
    fi
    ```
    + 使用管道时，对返回结果判断参考如下（尽量不使用过于复杂的管道）
    ```bash
    tar -cf - ./* | ( cd "${dir}" && tar -xf - )
    if [[ "${PIPESTATUS[0]}" -ne 0 || "${PIPESTATUS[1]}" -ne 0 ]]; then
      echo "Unable to tar files to ${dir}" >&2
    fi
    tar -cf - ./* | ( cd "${DIR}" && tar -xf - )
    return_codes=(${PIPESTATUS[*]})
    if [[ "${return_codes[0]}" -ne 0 ]]; then
      do_something
    fi
    if [[ "${return_codes[1]}" -ne 0 ]]; then
      do_something_else
    fi
    ```
2. 优先使用bash内部命令，内部命令更靠谱
    ```bash
     # Prefer this:
    addition=$((${X} + ${Y}))
    substitution="${string/#foo/bar}"
    
    # Instead of this:
    addition="$(expr ${X} + ${Y})"
    substitution="$(echo "${string}" | sed -e 's/^foo/bar/')"
    ```  

## 注释规范
1. 头文件
    每个脚本文件开始加注释用来解释该文件的功能
    ```bash
    #!/bin/bash
    #
    # Perform hot backups of Oracle databases.    
    ```
2. 函数
    如果函数功能不能自描述、不明显，需要添加注释，包括以下内容
    + 函数作用
    + 使用和修改的全局变量
    + 参数
    + 返回
    ```bash
    #!/bin/bash
    #
    # Perform hot backups of Oracle databases.
    export PATH='/usr/xpg4/bin:/usr/bin:/opt/csw/bin:/opt/goog/bin'
    #######################################
    # Cleanup files from the backup dir
    # Globals:
    #  BACKUP_DIR
    #  ORACLE_SID
    # Arguments:
    #  None
    # Returns:
    #  None
    #######################################
    cleanup() {
      ...
    }
    ```
3. `重要,精妙,费解`的地方给注释,
4. TODO
    待review的地方添加`TODO`注释，确定后清除TODO注释
    ```bash  
    # TODO(mrmonkey): Handle the unlikely edge cases (bug ####)
    ```
## 格式
1. 空格
    `不要`使用tab，使用2个空格
2. 长度
    一行不要超过80，如果过长，尽量拆成多行
3. 管道
    使用管道时，如果命令能一行放下就放一行，如果不能就每行放一个管道
    ```bash
    # All fits on one line
    command1 | command2
    # Long commands
    command1 \
      | command2 \
      | command3 \
      | command4
    ```
4. 循环
    使用if，循环的时候`; do`,`; then`和`while、for、if`放在同一行
    ```bash
    for dir in ${dirs_to_cleanup}; do
      if [[ -d "${dir}/${ORACLE_SID}" ]]; then
        log_date "Cleaning up old files in ${dir}/${ORACLE_SID}"
        rm "${dir}/${ORACLE_SID}/"*
        if [[ "$?" -ne 0 ]]; then
          error_message
        fi
      else
        mkdir -p "${dir}/${ORACLE_SID}"
        if [[ "$?" -ne 0 ]]; then
          error_message
        fi
      fi
    done
    ``` 
5. case
    + 开头使用`两个空格`，`不要使用tab`
    + 模式匹配不要以`左括号(`开头、不要出现`;&`和`;;&`
    + 放在同一行时，分隔符`;;`之前要加`空格`,例如
    ```bash
    verbose='false'
    aflag=''
    bflag=''
    files=''
    while getopts 'abf:v' flag; do
      case "${flag}" in
        a) aflag='true' ;;
        b) bflag='true' ;;
        f) files="${OPTARG}" ;;
        v) verbose='true' ;;
        *) error "Unexpected option ${flag}" ;;
      esac
    done
    ```
     + 一行放不下时，按照模式、执行语句、分隔符`;;`的格式分行
    ```bash
    case "${expression}" in
      a)
        variable="..."
        some_command "${variable}" "${other_expr}" ...
        ;;
      absolute)
        actions="relative"
        another_command "${actions}" "${other_expr}" ...
        ;;
      *)
        error "Unexpected expression '${expression}'"
        ;;
    esac
    ```   
6. 引用变量
    + 变量要加引用
    + 引用时优先加大括号,例如使用`"${var}"`不使用`"$var"`,下面的特例除外
    + 不给shell的特殊字符和位置参数加大括号,例如`$-,$?,$#,$1,$2`等
    + 不引起歧义的情况下,不要给单字符加,比如`"${1}0"`
    更多样例参考:
    ```bash
    # Section of recommended cases.
    
    # Preferred style for 'special' variables:
    echo "Positional: $1" "$5" "$3"
    echo "Specials: !=$!, -=$-, _=$_. ?=$?, #=$# *=$* @=$@ \$=$$ ..."
    
    # Braces necessary:
    echo "many parameters: ${10}"
    
    # Braces avoiding confusion:
    # Output is "a0b0c0"
    set -- a b c
    echo "${1}0${2}0${3}0"
    
    # Preferred style for other variables:
    echo "PATH=${PATH}, PWD=${PWD}, mine=${some_var}"
    while read f; do
      echo "file=${f}"
    done < <(ls -l /tmp)
    
    # Section of discouraged cases
    
    # Unquoted vars, unbraced vars, brace-quoted single letter
    # shell specials.
    echo a=$avar "b=$bvar" "PID=${$}" "${1}"
    
    # Confusing use: this is expanded as "${1}0${2}0${3}0",
    # not "${10}${20}${30}
    set -- a b c
    echo "$10$20$30"
    ```
7. 引号规则
    + 包含`变量,命令,空格,shell元字符`的语句`必须`加引号,除非有足够的理由
    + 字符串要加引号
    + 不要给数字型加引号,会被解析成字符串
    + `引号`和`[[`同时出现时要注意
    + 没有足够的理由使用`$@`,不要使用`$*`,参考https://stackoverflow.com/questions/9915610/the-difference-between-and
    ```bash
    # 'Single' quotes indicate that no substitution is desired.
    # "Double" quotes indicate that substitution is required/tolerated.

    # Simple examples
    # "quote command substitutions"
    flag="$(some_command and its args "$@" 'quoted separately')"

    # "quote variables"
    echo "${flag}"

    # "never quote literal integers"
    value=32
    # "quote command substitutions", even when you expect integers
    number="$(generate_number)"

    # "prefer quoting words", not compulsory
    readonly USE_INTEGER='true'

    # "quote shell meta characters"
    echo 'Hello stranger, and well met. Earn lots of $$$'
    echo "Process $$: Done making \$\$\$."

    # "command options or path names"
    # ($1 is assumed to contain a value here)
    grep -li Hugo /dev/null "$1"

    # Less simple examples
    # "quote variables, unless proven false": ccs might be empty
    git send-email --to "${reviewers}" ${ccs:+"--cc" "${ccs}"}

    # Positional parameter precautions: $1 might be unset
    # Single quotes leave regex as-is.
    grep -cP '([Ss]pecial|\|?characters*)$' ${1:+"$1"}

    # For passing on arguments,
    # "$@" is right almost everytime, and
    # $* is wrong almost everytime:
    #
    # * $* and $@ will split on spaces, clobbering up arguments
    #  that contain spaces and dropping empty strings;
    # * "$@" will retain arguments as-is, so no args
    #  provided will result in no args being passed on;
    #  This is in most cases what you want to use for passing
    #  on arguments.
    # * "$*" expands to one argument, with all args joined
    #  by (usually) spaces,
    #  so no args provided will result in one empty string
    #  being passed on.
    # (Consult 'man bash' for the nit-grits ;-)

    set -- 1 "2 two" "3 three tres"; echo $# ; set -- "$*"; echo "$#, $@")
    set -- 1 "2 two" "3 three tres"; echo $# ; set -- "$@"; echo "$#, $@")
    ```

## 特性和坑
1. 命令替换
    使用小括号`$(command)`,不要用反引号\`
2. Test, [ and [[
    优先使用`[[...]]`,里面不会出现扩展名替换和字符串分割,还支持正则表达式,`[...]`容易出错
    ```bash
    # This ensures the string on the left is made up of characters in the
    # alnum character class followed by the string name.
    # Note that the RHS should not be quoted here.
    # For the gory details, see
    # E14 at https://tiswww.case.edu/php/chet/bash/FAQ
    if [[ "filename" =~ ^[[:alnum:]]+name ]]; then
      echo "Match"
    fi
    
    # This matches the exact pattern "f*" (Does not match in this case)
    if [[ "filename" == "f*" ]]; then
      echo "Match"
    fi
    
    # This gives a "too many arguments" error as f* is expanded to the
    # contents of the current directory
    if [ "filename" == f* ]; then
      echo "Match"
    fi
    ```
3. 字符串判断
    使用-n,-z判断空字符串,不要加无关字符换,参考如下,
    ```bash
    # Do this:
    if [[ "${my_var}" = "some_string" ]]; then
      do_something
    fi
    
    # -z (string length is zero) and -n (string length is not zero) are
    # preferred over testing for an empty string
    if [[ -z "${my_var}" ]]; then
      do_something
    fi
    
    # This is OK (ensure quotes on the empty side), but not preferred:
    if [[ "${my_var}" = "" ]]; then
      do_something
    fi
    
    # Not this:
    if [[ "${my_var}X" = "some_stringX" ]]; then
      do_something
    fi
    # Use this
    if [[ -n "${my_var}" ]]; then
      do_something
    fi
    
    # Instead of this as errors can occur if ${my_var} expands to a test
    # flag
    if [[ "${my_var}" ]]; then
      do_something
    fi
    ```
4. 文件名通配符
    使用通配符时显示指定路径,例如加上`./`,比较安全,见示例
    ```bash
    # Here's the contents of the directory:
    # -f  -r  somedir  somefile
    
    # This deletes almost everything in the directory by force
    psa@bilby$ rm -v *
    removed directory: `somedir'
    removed `somefile'
    
    # As opposed to:
    psa@bilby$ rm -v ./*
    removed `./-f'
    removed `./-r'
    rm: cannot remove `./somedir': Is a directory
    removed `./somefile'
    ```
5. eval
    `不允许`使用eval,不安全
6. 管道和while
    不要管道到while中作为循环输入,使用loops或者子进程来实现,因为管道到while中对变量的修改`不会`返回到当前shell中,容易出bug,也不容易debug
    ```bash
    last_line='NULL'
    your_command | while read line; do
      last_line="${line}"
    done
    
    # This will output 'NULL'
    echo "${last_line}"
    
    last_line='NULL'
    your_command | while read line; do
      last_line="${line}"
    done
    
    # This will output 'NULL'
    echo "${last_line}"
    
    total=0
    last_file=
    while read count filename; do
      total+="${count}"
      last_file="${filename}"
    done < <(your_command | uniq -c)
    
    # This will output the second field of the last line of output from
    # the command.
    echo "Total = ${total}"
    echo "Last one = ${last_file}"
    
    # Trivial implementation of awk expression:
    #  awk '$3 == "nfs" { print $2 " maps to " $1 }' /proc/mounts
    cat /proc/mounts | while read src dest type opts rest; do
      if [[ ${type} == "nfs" ]]; then
        echo "NFS ${dest} maps to ${src}"
      fi
    done
    ```

## Google完整规范参考
https://google.github.io/styleguide/shell.xml
