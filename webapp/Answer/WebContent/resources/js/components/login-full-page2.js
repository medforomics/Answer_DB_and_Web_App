

Vue.component('login-full-page2', {
    props: {
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: `<div >
    <v-toolbar fixed app flat>
        <div class="toolbar-image">
        <img class="toolbar-image pt-1 pb-1" :src="dataUrlRoot + '/resources/images/answer-logo-icon-medium.png'"/>
        </div>
      <v-toolbar-title class="headline">
      Answer
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <div class="toolbar-image">
            <img class="toolbar-image pt-2 pb-2" alt="ngs logo" :src="dataUrlRoot + '/resources/images/screenshots/NGS_Lab_Color.png'">
            <img class="toolbar-image" alt="utsw master logo" :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'">
        </div>
        <v-btn flat @click="showLoginDialog = true" v-show="showLogin" dark class="teal lighten-2">Login</v-btn>
    </v-toolbar>
    <v-snackbar :timeout="0" :bottom="true" :value="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

    <v-dialog v-model="showResetPasswordDialog" max-width="50%" scrollable>
    <v-card>
      <v-toolbar dense dark color="teal lighten-2">
        <v-toolbar-title class="white--text">Reset Your Password</v-toolbar-title>
      </v-toolbar>
      <v-card-title>Send a reset link to this email address:</v-card-title>
      <v-card-text>
        <v-layout row wrap class="pl-2">
          <v-flex xs12 lg6 >
          <v-text-field label="email" 
          v-model="email"
          required></v-text-field>
          </v-flex>
        </v-layout>
      </v-card-text>
      <v-card-actions>
        <v-btn class="mr-2" color="success" @click="sendResetPasswordEmail()" slot="activator"
        :disabled="!email">Send
          <v-icon right dark>email</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelReset()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
    <v-container fluid grid-list-xl>
    <v-layout row wrap justify-center pb-3>
    <v-flex class="display-1 text-xs-center" xs8>
      {{ headerText }}
    </v-flex>
    <v-layout row wrap pb-5>
    <v-flex v-for="(card, index) in cards" :key="index" xs12 md4 lg4 xl4>
    <v-hover>
    <v-card slot-scope="{ hover }"  :class="[hover ? showScreenshotsAndHighlight(index) : 'elevation-0', 'no-background pl-2 pr-2 pt-2 pb-2']" >
            <div class="title pb-3"> <v-icon>{{ icons[index] }} </v-icon><span class="pl-2">{{ card }}<span></div>
            <div class="subheading" color="blue-grey lighten-2">
            {{ subTexts[index] }}</div>
            <v-card>   
            </v-hover>  
    </v-flex>
    </v-layout> 

    <v-layout row wrap justify-center pb-5>
    <v-flex :class="currentFlex" v-for="img in currentImgs" :key="img">
    <v-card flat tile class="no-background">
          <v-img alt="snp filtering" :src="dataUrlRoot + '/resources/images/screenshots/' + img"></v-img>
          <v-card>
          </v-flex>
          </v-layout> 
     
    </v-container>
      <v-layout row justify-center pt-3 v-if="allowResetPwd">
        <v-flex xs12 md6 lg6 xl6 class="text-xs-center">
          <a @click="openResetPasswordDialog">Reset Password</a>
        </v-flex>
      </v-layout>
      <v-dialog v-model="showLoginDialog" max-width="300px">
          <v-card dark class="teal darken-2">
          <v-card-content class="text-xs-center">
              <login :message="message" :popup="false" :authType="authType"></login>
          </v-card-content>
          </v-card>
      </v-dialog>
</div>`,
    data() {
        return {
            magicClass: "",
            elevation: "elevation-1",
            changingVersion: false,
            versionName: "1.0",
            message: authMessage,
            authType: authType,
            showResetPasswordDialog: false,
            email: "",
            snackBarVisible: false,
            snackBarMessage: "",
            allowResetPwd: false,
            showLogin: false,
            showLoginDialog: false,
            filterImgs: ["filter1.png", "filter2.png", "filter3.png", "filter4.png"],
            externalResoucesImgs: ["igv.png", "musica.png"],
            annotationImgs: ["trials.png", "annotations.png"],
            variantsCNVsImgs: ["snps.png", "cnv.png"],
            reportImgs: ["report_html_1.png", "report_html_2.png", "report_pdf_1.png", "report_pdf_2.png"],
            headerText: "Answer is comprehensive tool to visualize and annotate variants for Clinical Reporting",
            cards:   [
                "Filter thousands of variants",
                "Connect to outside resources",
                "Get detailed insights about each variant",
                "Annotations and Collaborations",
                "Meaningful Reports"
            ],
            subTexts: [
                "Focus on impactful mutations with simple yet powerful filters.",
                "Open variants on external databases and visualizations such as the UCSC Genome Browser, ClinVar, COSMIC, MuSiCa, IGV...",
                "Browse SNPs, Indels, CNS, Fusion and Translocations.",
                "Create, share, and browse annotations, and link mutations to existing clinical trials.",
                "Select variants and annotations to create meaningful reports that can be exported to PDFs."
            ],
            icons: [
                "mdi-magnify-plus-outline",
                "mdi-toolbox-outline",
                "mdi-eye",
                "mdi-message-reply-text",
                "mdi-pdf-box",

            ],
            carousel: [],
            currentIndex: -1,
            currentImgs: ["answer-logo-large.png"],
            currentFlex: "xs6 md6 lg3 xl3",
            currentVisibility: [true]
        }
    },
    methods: {
        checkAlreadyLoggedIn() {
            axios.get(
                webAppRoot + "/checkAlreadyLoggedIn",
                {
                    params: {
                    }
                })
                .then(response => {
                    if (response.data.success) {
                        if (this.$route.query.urlRedirect) {
                            window.location = this.$route.query.urlRedirect;
                        }
                        else {
                            window.location = response.data.urlRedirect;
                        }
                    }
                    else {
                        this.showLogin = true;
                        this.message = this.message ? this.message : response.data.reason;
                        console.log(response.data.reason);
                        this.allowResetPwd = response.data.payload == "local";
                        this.authType = response.data.payload;
                    }
                }).catch(error => {
                });
        },
        switchToReleaseVersion() {
            this.magicClass = "magic-wand";
            this.changingVersion = true;
        },
        handleVersionChange() {
            if (this.changingVersion) {
                this.changingVersion = false;
                this.magicClass = "";
                this.$refs.goodiesPanel.createFireworks();
                this.updateToVersion1();
                setTimeout(this.$refs.goodiesPanel.clearFireworks, 10000);
            }
            else {
                if (Date.now() >= new Date("02/27/2019").getTime()) {
                    this.switchToReleaseVersion();
                }
            }
        },
        updateToVersion1() {
            axios.get(
                webAppRoot + "/updateVersion",
                {
                    params: {
                    }
                })
                .then(response => {
                    if (response.data.success) {
                        this.getVersion();
                    }
                    else {
                        console.log(response.data.reason);
                    }
                }).catch(error => {
                });
        },
        isBetaVersion() {
            return this.versionName == "beta";
        },
        isVersionOne() {
            return this.versionName == "1.0";
        },
        getVersion() {
			axios.get(webAppRoot + "/getCurrentVersion", {
				params: {}
			})
            .then(response => {
                if (response.data.isAllowed) {
                    this.versionName = response.data.payload;
                }
                else {
                    console.log(response.message);
                }
            })
            .catch(error => {
                alert(error);
            });
        },
        openResetPasswordDialog() {
            this.email = "";
            this.showResetPasswordDialog = true;
        },
        cancelReset() {
            this.showResetPasswordDialog = false;
        },
        sendResetPasswordEmail() {
            axios({
                method: 'post',
                url: webAppRoot + "/sendResetPasswordEmail",
                params: {
                    email: this.email,
                },
                data: {
                }
            }).then(response => {
                if (response.data.success) {
                    this.snackBarMessage = "The reset email was sent";
                    this.cancelReset();
                }
                else if (response.data.message) {
                    this.snackBarMessage = response.data.message;
                }
                else {
                    this.snackBarMessage = "Unknown email address or could not send email";
                }
                this.snackBarVisible = true;
            }
            ).catch(error => {
                alert(error);
            }
            );
        },
        showScreenshotsAndHighlight(index) {
            this.currentIndex = index;
            this.currentImgs = this.carousel[index].imgs;
            this.currentFlex = this.carousel[index].flex;
            return "elevation-6";
        },
        populateCarousel() {
            this.carousel = [
                {imgs: this.filterImgs, flex: "xs12 md6 lg3 xl2"}, 
                {imgs: this.externalResoucesImgs, flex: "xs12 md12 lg6 xl6"}, 
                {imgs: this.variantsCNVsImgs, flex: "xs12 md12 lg6 xl6"}, 
                {imgs: this.annotationImgs, flex: "xs12 md12 lg6 xl6"}, 
                {imgs: this.reportImgs, flex: "xs12 md12 lg6 xl6"}, 
            ];
        },
    },
    mounted: function () {
       this.populateCarousel();
    },
    computed: {

    },
    created: function () {
        this.checkAlreadyLoggedIn();
        this.getVersion();
    },
})