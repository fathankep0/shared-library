import jenkins.model.*

def call (){
    pipeline {
        agent any
        environment {
                // repo ANSIBLE
                def GITLAB = "git@gitlab.com"
                def GITLAB_GROUP = "prosperosolution"
        	    def GITLAB_INFRA_GROUP = 'devsecops'
        	    def GITLAB_REPOSITORY_ANSIBLE = 'infra-as-code'
        	    def GITLAB_ANSIBLE = "${GITLAB}:${GITLAB_GROUP}/${GITLAB_INFRA_GROUP}/${GITLAB_REPOSITORY_ANSIBLE}.git"
                def GITLAB_BRANCH_ANSIBLE = "development"
                def GITLAB_URL_ANSIBLE = "infra-as-code-development"
        	    //def GITLAB_INFRA_REPO = "${GITLAB}/${GITLAB_INFRA_GROUP}/${GITLAB_REPOSITORY_ANSIBLE}/-/archive/${GITLAB_BRANCH_ANSIBLE}/${GITLAB_URL_ANSIBLE}.zip"
                def GITLAB_INFRA_REPO = "${GITLAB_ANSIBLE}"
            }
            /*parameters {
                choice(name: 'projectName', choices: "build_images", description: 'Typing project_name, example build-images')
                choice(name: 'tagName', choices: "${env.TAG_NAME}", description: "OutPut branch_name, example feature/*")
                choice(name: 'userName', choices: "mcustomer", description: "Typing username, example mcustomer")    
            }*/
        stages { 
                stage('Parameters'){
                    steps {
                        script {
                            properties([
                                    parameters([
                                        [$class: 'ChoiceParameter', 
                                            choiceType: 'PT_SINGLE_SELECT', 
                                            description: 'Select the Environemnt from the Dropdown List', 
                                            filterLength: 1, 
                                            filterable: false, 
                                            name: 'USERNAME',
                                            script: [
                                                $class: 'GroovyScript', 
                                                fallbackScript: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script: 
                                                        "return['Could not get The environemnts USERNAME']"
                                                ], 
                                                script: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script: 
                                                        'return["jenkins"]'
                                                ]
                                            ]
                                        ],
                                        [$class: 'ChoiceParameter', 
                                            choiceType: 'PT_SINGLE_SELECT', 
                                            description: 'Select the Environemnt from the Dropdown List', 
                                            filterLength: 1, 
                                            filterable: false, 
                                            name: 'ENV',
                                            script: [
                                                $class: 'GroovyScript', 
                                                fallbackScript: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script: 
                                                        "return['Could not get The environemnts ENV']"
                                                ], 
                                                script: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script: 
                                                        'return["development"]'
                                                ]
                                            ]
                                        ],
                                        [$class: 'CascadeChoiceParameter', 
                                            choiceType: 'PT_SINGLE_SELECT', 
                                            description: 'Select Server Name from the Dropdown List',
                                            filterLength: 1, 
                                            filterable: false, 
                                            name: 'SERVER_NAME',
                                            referencedParameters: 'ENV',
                                            script: 
                                                [$class: 'GroovyScript', 
                                                fallbackScript: [
                                                        classpath: [], 
                                                        sandbox: false, 
                                                        script: "return['Could not get Environment from SERVER_NAME Param']"
                                                        ], 
                                                script: [
                                                        classpath: [], 
                                                        sandbox: false, 
                                                        script: '''
                                                        if (ENV.equals("development")){
                                                            return["backend", "frontend" ]
                                                        }
                                                        '''
                                                    ] 
                                            ]
                                        ],
                                        [$class: 'CascadeChoiceParameter', 
                                            choiceType: 'PT_SINGLE_SELECT', 
                                            description: 'Select the Environemnt from the Dropdown List', 
                                            filterLength: 1, 
                                            filterable: false, 
                                            name: 'PROJECT_GROUP',
                                            referencedParameters: 'ENV',
                                            script: [
                                                $class: 'GroovyScript', 
                                                fallbackScript: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script: 
                                                        "return['Could not get The environemnts PROJECT_GROUP']"
                                                ], 
                                                script: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script: '''
                                                        if (ENV.equals("development")){
                                                            return["demo"]
                                                        }
                                                        '''
                                                ]
                                            ]
                                        ],
                                        [$class: 'DynamicReferenceParameter', 
                                            choiceType: 'ET_FORMATTED_HTML', 
                                            description: 'Select the Environemnt from the Dropdown List', 
                                            omitValueField: true,
                                            filterLength: 1, 
                                            filterable: false, 
                                            name: 'TAGGING_NUMBER',
                                            referencedParameters: 'ENV',
                                            script: [
                                                $class: 'GroovyScript', 
                                                fallbackScript: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script: 
                                                        "return['Could not get The CREATE_TAGGING Param']"
                                                ], 
                                                script: [
                                                    classpath: [], 
                                                    sandbox: false, 
                                                    script:'''
                                                        if(ENV.equals("development")){
                                                            inputBox="<input name='value' type='text'>"
                                                        }
                                                    '''
                                                ]
                                            ]
                                        ],
                                        [$class: 'DynamicReferenceParameter', 
                                            choiceType: 'ET_ORDERED_LIST', 
                                            description: 'Select the  Number Of Instance based on the following information', 
                                            name: 'PLEASE READ this information !!!!!!!!!!!!!!!!!!!!!!', 
                                            referencedParameters: 'ENV', 
                                            script: 
                                                [$class: 'GroovyScript', 
                                                script: 'return["Could not get Information DynamicReferenceParameter"]', 
                                                script: [
                                                    script: '''
                                                            if(ENV.equals("demo")){
                                                                return["frontend: 10.184.0.4"]
                                                            }
                                                            else if(ENV.equals("demo")){
                                                                return["backend: 10.184.0.4"]
                                                            }
                                                            '''
                                                        ]
                                                ]
                                        ]
                                    ])
                                ])
                            }
                        }
                    }         
                stage('Preparation Git Clone'){
        			steps{
        				script{
                            // repo PROJECT_NAME
                            def PROJECT_BRANCH = "${env.GIT_BRANCH}"
                            def PROJECT_ENV = "${params.ENV}"
                            def PROJECT_GROUP = "${params.PROJECT_GROUP}"
                            def PROJECT_NAME = "${params.PROJECT_NAME}"
                            def PROJECT_TAGGING = "${env.TAGGING_NUMBER}"
                            def SERVER_NAME = "${params.SERVER_NAME}"
                            def STACK_NAME = "${params.STACK}"
                            def USER_NAME = "${params.USERNAME}"
                            //NOTIF_MSG="Build #${BUILD_NUMBER} on STAGE PREPARATION - "
                            git branch: "${GITLAB_BRANCH_ANSIBLE}",
                                credentialsId: 'GIT_CREDENTIALS',
                                url: "${GITLAB_ANSIBLE}"
                            sh """ 
                            printenv | sort
                            git submodule update --init --recursive
                            echo BRANCHNAME: ${PROJECT_BRANCH}
                            echo PROJECTENV: ${PROJECT_ENV}
                            echo PROJECTGROUP: ${PROJECT_GROUP}
                            echo PROJECTNAME: ${PROJECT_NAME}
                            echo SERVER_NAME: ${SERVER_NAME}
                            echo TAGGINGNAME: ${PROJECT_TAGGING}
                            echo USER_NAME: ${USER_NAME}
                            """
                        }
                    }
                }
                stage('Deployment'){
        		    steps{
                        script {
                            //NOTIF_MSG="Build #${BUILD_NUMBER} on STAGE DEPLOYMENT - "
                            def PROJECT_BRANCH = "${env.GIT_BRANCH}"
                            def PROJECT_ENV = "${params.ENV}"
                            def PROJECT_GROUP = "${params.PROJECT_GROUP}"
                            def PROJECT_NAME = "${params.PROJECT_NAME}"
                            def PROJECT_TAGGING = "${env.TAGGING_NUMBER}"
                            def SERVER_NAME = "${params.SERVER_NAME}"
                            def STACK_NAME = "${params.STACK}"
                            def USER_NAME = "${params.USER_NAME}"
                            stage("Running Playbook ${PROJECT_NAME}"){
                                script {
                                    //NOTIF_MSG="Build #${BUILD_NUMBER} on STAGE ${PROJECT_NAME} - Deployment - "
                                    dir("${WORKSPACE}") {
                                        withCredentials([
                                            file(credentialsId: 'vault', variable: 'vault')]){
                                                // Ansible vault key password
                                                sh 'mv "${vault}" ansible_vault'
                                                def filePathVault = readFile "ansible_vault"
                                                def pass_vault = filePathVault.readLines()
                                                sh """
                                                cd ${HOME}/ansible
                                                . hacking/env-setup -q
                                                cd ${WORKSPACE}
                                                time ansible-playbook \
                                                    -e '{"paramsGitUrl":"${GIT_URL}"}' \
                                                    -e '{"paramsHomeJenkins":"${JENKINS_HOME}"}' \
                                                    -e '{"paramsPWD":"${PWD}"}' \
                                                    -e '{"paramsProjectBranch":"${PROJECT_BRANCH}"}' \
                                                    -e '{"paramsProjectEnv":"${PROJECT_ENV}"}' \
                                                    -e '{"paramsProjectGroup":"${PROJECT_GROUP}"}' \
                                                    -e '{"paramsProjectName":"${PROJECT_NAME}"}' \
                                                    -e '{"paramsProjectTagging":"${PROJECT_TAGGING}"}' \
                                                    -e '{"paramsSTACK":"${STACK_NAME}"}' \
                                                    -e '{"paramsServerName":"${SERVER_NAME}"}' \
                                                    -e '{"paramsUserJenkins":"${USER}"}' \
                                                    -e '{"paramsUserName":"${USER_NAME}"}' \
                                                    -e @${WORKSPACE}/env_vars/env/${PROJECT_GROUP}.json \
                                                    -e @${WORKSPACE}/env_vars/env/all.json \
                                                    -i "${WORKSPACE}/inventory.yml" \
                                                    ${WORKSPACE}/playbook/microservices/${PROJECT_ENV}_${PROJECT_GROUP}_${SERVER_NAME}.yml --vault-password-file ansible_vault --flush-cache
                                                """
                                            }
                                        }
                                    }   
                                }
                            }
                        }
                    }
                }
        post{
            always{
    	        cleanWs()
                }
    	    }
        }
}
