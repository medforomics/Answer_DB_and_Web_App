

Vue.component('existing-reports', {
    props: {
        currentReportId: {default: "", type: String},
        readonly: {default: true, type: Boolean},
        reportUnsaved: {default: true, type: Boolean},
        loadingReportDetails: {default: true, type: Boolean},
    },
    template: /*html*/`<div>
    
    <v-dialog v-model="finalizeConfirmationVisible" max-width="700px">
    <v-toolbar dense dark color="primary">
        <v-toolbar-title>
            Finalize Report
        </v-toolbar-title>
    </v-toolbar>
    <v-card>
        <v-card-text class="pl-2 pr-2 subheading">
            Finalizing a report is <b>permanent</b>. No other report can be finalized.<br/>
            Only amendments and addenda will be allowed after this step.<br/>
            <br/>
            Please type <b><span v-text="getCurrentUserFullName()"></span></b> to confirm:
            <v-text-field browser-autocomplete="new-password" autocomplete="new-password"
                v-model="fullNameField" class="mr-2 no-height" label="Full Name">
            </v-text-field>
        </v-card-text>
        <v-card-actions class="card-actions-bottom">
            <v-btn class="mr-2" color="warning" @click="finalizeReport()" slot="activator" :disabled="!readyToFinalize()" :loading="finalizingReport">Finalize
            </v-btn>
            <v-btn class="mr-2" color="error" @click="cancelFinalizeReport()" slot="activator" :disabled="finalizingReport">Cancel
            </v-btn>
        </v-card-actions>
    </v-card>
</v-dialog>


<!-- Admendment Dialog -->
<v-dialog v-model="amendmentDialogVisible" max-width="700px">
<v-toolbar dense dark color="primary">
    <v-toolbar-title>
        Amend Report
    </v-toolbar-title>
</v-toolbar>
<v-card>
    <v-card-text class="pl-2 pr-2 subheading">
    How does it work?<br/><br/>
        - amending a report will create a new report with the latest case annotations.<br/>
        - the finalized report (<b>{{ amendingReportName }}</b>)
        will be marked as amended and the new report will need to be saved and finalized.<br/>
        <br/>
        Please state the reason for this amendment (required):
        <v-textarea textarea
            v-model="amendmentReason" label="Amendment Reason">
        </v-textarea>
    </v-card-text>
    <v-card-actions class="card-actions-bottom">
        <v-btn class="mr-2" color="warning" @click="amendReport()" slot="activator" :disabled="!amendmentReason" :loading="amendingReportLoading">Confirm
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelAmendingReport()" slot="activator" :disabled="amendingReportLoading">Cancel
        </v-btn>
    </v-card-actions>
</v-card>
</v-dialog>

<!-- Addendum Dialog -->
<v-dialog v-model="addendumDialogVisible" max-width="700px">
<v-toolbar dense dark color="primary">
    <v-toolbar-title>
        Addend Report
    </v-toolbar-title>
</v-toolbar>
<v-card>
    <v-card-text class="pl-2 pr-2 subheading">
        How does it work?<br/><br/>
        - you are about to <b>unfinalize</b> report <b>{{ addendingReportName }}</b>.<br/>
        - only <b>new annotations and clinical trials</b> will be added.<br/>
        - the report will keep all existing notes and variants, <b>no new variants</b> can be added.<br/>
        - click on the report's <b>Update Addendum</b> button to pull in new annotations.<br/>
        - only the <b>Addendum Summary</b> can be modified.<br/><br/>
        This action is irreversible.
    </v-card-text>
    <v-card-actions class="card-actions-bottom">
        <v-btn class="mr-2" color="warning" @click="addendReport()" slot="activator" :loading="addendingReportLoading">Confirm
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelAddendingReport()" slot="activator" :disabled="addendingReportLoading">Cancel
        </v-btn>
    </v-card-actions>
</v-card>
</v-dialog>

    <v-card class="soft-grey-background">
    <v-toolbar class="elevation-0" dense dark color="primary">
        <v-menu offset-y offset-x class="ml-0">
            <v-btn slot="activator" flat icon dark>
                <v-icon color="amber accent-2">assignment</v-icon>
                <v-icon color="amber accent-2" class="multi-icon">assignment</v-icon>
            </v-btn>
            <v-list>
                <v-list-tile avatar @click="closeExistingReport()">
                    <v-list-tile-avatar>
                        <v-icon>cancel</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Close Existing Reports</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-menu>
        <v-toolbar-title class="ml-0">Existing Reports</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn flat icon @click="closeExistingReport()" slot="activator">
                <v-icon>close</v-icon>
            </v-btn>
            <span>Close Existing Reports</span>
        </v-tooltip>
    </v-toolbar>
    <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
    <v-layout row wrap>
    <v-flex xs12>
        <v-btn @click="getReportDetails()" :disabled="loadingReportDetails">New Report</v-btn>
    </v-flex>
    <v-flex xs12 lg4 xl3 v-for="report in existingReports" :key="report._id['$oid']">
        <v-card :color="isReportSelected(report) ? 'amber accent-2' : ''">
            <v-card-text>
            <v-list :class="['dense-tiles', isReportSelected(report) ? 'amber accent-2' : '']" :color="isReportSelected(report) ? 'amber accent-2' : ''">
            <v-list-tile v-if="needsSpecialHeader(report)">
            <v-list-tile-content class="pb-2">
                <v-layout class="full-width" justify-space-between>
                    <v-flex class="text-xs-left xs">
                        <span class="'selectable'" v-if="isPureFinalized(report)">
                            <b>Finalized <span v-text="parseFinalizedDate(report)"></span></b>
                            <v-icon color="primary" class="pb-1">check</v-icon>
                        </span>
                        <span class="'selectable'" v-if="report.amended">
                        <b>Amended </b>(replaced by {{ report.supersededByReportId }})<br/><b>Reason:</b> {{ report.amendmentReason }}
                        </span>
                        <span class="'selectable'" v-if="isReplacingAmended(report)">
                        <b>To Finalize </b>(replacing {{ report.supersedsReportId }})
                        </span>
                        <v-tooltip bottom v-if="!report.finalized && report.addendum">
                            <span slot="activator" class="'selectable'">
                            <b>Report with Addendum</b>
                            <v-icon color="primary" class="pb-1">mdi-square-edit-outline</v-icon>
                            </span>
                            <span>This report contains an addendum.<br/>
                            You can only modify the Addendum Summary.
                            </span>
                        </v-tooltip>
                    </v-flex>
                </v-layout>
            </v-list-tile-content>
            </v-list-tile>
                <v-list-tile>
                    <v-list-tile-content class="pb-2">
                        <v-layout class="full-width" justify-space-between>
                            <v-flex class="text-xs-left xs">
                                <span class="'selectable'">Report Name:</span>
                            </v-flex>
                            <v-flex class="blue-grey--text text--lighten-1 text-xs-right">
                                <span class="selectable">{{ report.reportName }}</span>
                            </v-flex>
                        </v-layout>
                    </v-list-tile-content>
                </v-list-tile>
                <v-list-tile>
                <v-list-tile-content class="pb-2">
                    <v-layout class="full-width" justify-space-between>
                        <v-flex class="text-xs-left xs">
                            <span class="selectable">Date Modified:</span>
                        </v-flex>
                        <v-flex class="text-xs-right blue-grey--text text--lighten-1">
                            <span class="selectable" v-text="parseDate(report)"></span>
                        </v-flex>
                    </v-layout>
                </v-list-tile-content>
                </v-list-tile>
                <v-list-tile>
                <v-list-tile-content class="pb-2">
                    <v-layout class="full-width" justify-space-between>
                        <v-flex class="text-xs-left xs">
                            <span class="selectable">Modified By:</span>
                        </v-flex>
                        <v-flex class="text-xs-right blue-grey--text text--lighten-1">
                            <span class="selectable">{{ report.modifiedByName }}</span>
                        </v-flex>
                    </v-layout>
                </v-list-tile-content>
                </v-list-tile>
                <v-list-tile>
                <v-list-tile-content class="pb-2">
                    <v-layout class="full-width" justify-space-between>
                        <v-flex class="text-xs-left xs">
                            <span class="'selectable'">Notes:</span>
                        </v-flex>
                        <v-flex class="text-xs-right blue-grey--text text--lighten-1">
                        <v-tooltip bottom max-width="600px">
                            <span slot="activator" class="selectable" v-text="truncatedNotes(report)"></span>
                            <span>{{ report.summary }}</span>    
                        </v-tooltip>
                        </v-flex>
                    </v-layout>
                </v-list-tile-content>
                </v-list-tile>
            </v-list>
            </v-card-text>

            <v-card-actions class="card-actions-bottom">
            <v-layout row wrap>
            <v-flex>
            <v-btn @click="getReportDetails(report._id['$oid'])"
            :disabled="isReportSelected(report) || loadingReportDetails">
            Load Report
            </v-btn>
            </v-fleX>

            <v-flex>
            <v-tooltip bottom>
            <v-btn slot="activator" @click="openFinalizeConfirmationDialog(report._id['$oid'])"
            :disabled="finalizeButtonDisabled(report)">
            Finalize
            </v-btn>
            <span v-text="getFinalizedButtonTooltip(report)"></span>
            </v-tooltip>
            </v-fleX>

            <v-flex>
            <v-tooltip bottom v-if="report.finalized && !report.addendum && !report.amended">
            <v-btn slot="activator" @click="openAmendmentConfirmationDialog(report)"
            :disabled="!canBeAmended(report)">Amendment...</v-btn>
            <span v-if="canBeAmended(report)">Amend the report.<br/>
            THIS WILL CREATE AN ENTIRELY NEW REPORT FROM THE LATEST ANNOTATIONS</span>
            <span v-if="!canBeAmended(report)">You cannot amend this report.</span>
            </v-tooltip>
            </v-fleX>

            <v-flex>
            <v-tooltip bottom v-if="report.finalized && !report.amended && !report.addendum">
            <v-btn slot="activator" @click="openAddendumConfirmationDialog(report)"
            :disabled="!canBeAddended(report)">Start Addendum...</v-btn>
            <span v-if="canBeAddended(report)">Add information the report<br/>
            Only new annotations on selected variants can be added.<br/></span>
            <span v-if="!canBeAddended(report)">You cannot modify this report.</span>
            </v-tooltip>
            </v-fleX>

            <v-flex>
            <v-tooltip bottom v-if="canBeUpdated(report)">
            <v-btn slot="activator" @click="updateAddendum(report)" :loading="addendingReportLoading">Update Addendum</v-btn>
            <span>Update with latest annotations</span>
            </v-tooltip>
            </v-fleX>

            </v-layout>



            </v-card-actions>

        </v-card>
    </v-flex>
    </v-layout>
    </v-container>
    </v-card>
    </div>
    `,
    data() {
        return {
            existingReports: [],
            finalizeConfirmationVisible: false,
            reportIdToFinalize: null,
            fullNameField: "",
            canFinalize: false,
            finalizingReport: false,
            amendingReportName: null,
            amendingReportId: null,
            amendingReportLoading: false,
            amendmentDialogVisible: false,
            amendmentReason: "",
            addendingReportName: null,
            addendingReportId: null,
            addendingReportLoading: false,
            addendumDialogVisible: false,


        }
    },
    methods: {
        closeExistingReport() {
            this.$emit("close-existing-reports");
        },
        getReportDetails(reportId) {
            this.$emit("get-report-details", reportId);
        },
        handleLoadingReportDetails(isLoading) {
            this.$emit("loading-report-details", isLoading);
        },
        getExistingReports() {
            axios.get(
                webAppRoot + "/getExistingReports",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.existingReports = response.data.reports;
                        // this.existingReports.forEach((r) => {console.log(r._id['$oid'])});
                        this.canFinalize = !this.finalizeReportExists();
                        this.getReportDetails(this.$route.query.reportId);
                    }
                    else {
                        this.handleDialogs(response.data, this.getExistingReports);
                        this.handleLoadingReportDetails(false);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                    this.handleLoadingReportDetails(false);
                });
        },
        isReportSelected(report) {
            return report && report._id.$oid == this.currentReportId;
        },
        truncatedNotes(report) {
            if (report.summary && report.summary.length > 50) {
                return report.summary.substring(0, 50) + "...";
            }
            return report.summary;
        },
        parseDate(report) {
            if (report.dateModified) {
                return report.modifiedSince + " (" + report.dateModified.split("T")[0] + ")";
            }
        },
        parseFinalizedDate(report) {
            if (report.dateFinalized) {
                return report.finalizedSince + " (" + report.dateFinalized.split("T")[0] + ")";
            }
        },
        handleDialogs(response, callback) {
            if (response == "not-allowed") {
                bus.$emit("not-allowed", [this.response]);
            }
            if (response.isXss) {
                bus.$emit("xss-error", [null, response.reason]);
            }
            else if (response.isLogin) {
                bus.$emit("login-needed", [null, callback])
            }
            else if (response.success === false) {
                bus.$emit("some-error", [null, response.message]);
            }
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [null, error]);
        },
        readyToFinalize() {
            var nameMatch = this.getCurrentUserFullName().toUpperCase() == this.fullNameField.toUpperCase();
            return this.reportIdToFinalize && !this.readonly && nameMatch;
        },
        cancelFinalizeReport() {
            this.finalizeConfirmationVisible = false;
            this.fullNameField = "";
            this.reportIdToFinalize = null;
        },
        finalizeReport() {
            if (!this.readyToFinalize()) {
                return;
            }
            this.finalizingReport = true;
            axios({
                method: 'post',
                url: webAppRoot + "/finalizeReport",
                params: {
                    reportId: this.reportIdToFinalize,
                },
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        snackBarMessage = "Report Finalized";
                        snackBarVisible = true;
                        this.getExistingReports();
                        this.finalizeConfirmationVisible = false;
                    } else {
                        this.handleDialogs(response.data, this.finalizeReport);
                        this.finalizeConfirmationVisible = false;
                    }
                    this.finalizingReport = false;
                })
                .catch(error => {
                    this.finalizingReport = false;
                    this.finalizeConfirmationVisible = false;
                    this.handleAxiosError(error);
                });
        },
        openFinalizeConfirmationDialog(reportId) {
            this.reportIdToFinalize = reportId;
            this.fullNameField = "";
            this.finalizeConfirmationVisible = true;
        },
        finalizeReportExists() {
            for (var i = 0; i < this.existingReports.length; i++) {
                var r = this.existingReports[i];
                if (r.finalized && !r.amended) {
                    return true;
                }
            }
            // return false;
        },
        finalizeButtonDisabled(report) {
            if (this.readonly) {
                return true;
            }
            var currentReportLoaded = report._id['$oid'] == this.currentReportId;
            if (!currentReportLoaded) {
                return true;
            }
            return (!this.canFinalize 
            || this.reportUnsaved
            || report.finalized)
            && !(report.addendum && !report.finalized);
        },
        getCurrentUserFullName() {
            return userFullName; //global variable
        },
        canBeAmended(report) { //can only amend finalized reports once
            return !this.readonly && 
            !this.reportUnsaved && 
            report.finalized && 
            !report.amended
            && report._id['$oid'] == this.$route.query.reportId; //verify that the selected report is one clicked on
        },
        openAmendmentConfirmationDialog(report) {
            this.amendingReportName = report.reportName;
            this.amendingReportId = report._id['$oid'];
            this.amendmentDialogVisible = true;
        },
        amendReport() {
            if (this.readonly) {
                return;
            }
            this.$emit("amend-report", this.amendingReportId);
        },
        cancelAmendingReport() {
            this.amendingReportName = null;
            this.amendingReportId = null;
            this.amendingReportLoading =  false;
            this.amendmentDialogVisible = false;
        },
        openAddendumConfirmationDialog(report) {
            this.addendingReportName = report.reportName;
            this.addendingReportId = report._id['$oid'];
            this.addendumDialogVisible = true;
        },
        addendReport() {
            if (this.readonly) {
                return;
            }
            this.$emit("addend-report", this.addendingReportId);
        },
        updateAddendum(report) {
            this.addendingReportId = report._id['$oid'];
            this.addendReport();
        },
        cancelAddendingReport() {
            this.addendingReportName = null;
            this.addendingReportId = null;
            this.addendingReportLoading =  false;
            this.addendumDialogVisible = false;
        },
        canBeAddended(report) {
            return !this.readonly && 
            !this.reportUnsaved && 
            report.finalized && 
            !report.addendum
            && report._id['$oid'] == this.$route.query.reportId; //verify that the selected report is one clicked on
        },
        canBeUpdated(report) {
            return !this.readonly && 
            !this.reportUnsaved && 
            !report.finalized && 
            report.addendum
            && report._id['$oid'] == this.$route.query.reportId;
        },
        isPureFinalized(report) {
            return  report.finalized && !report.amended;
        },
        //replaces an amended report but not yet finalized
        isReplacingAmended(report) {
            return !report.finalized && report.supersedsReportId;
        },
        needsSpecialHeader(report) {
            return  report.finalized || report.amended || report.addendum || report.supersedsReportId;
        },
        getFinalizedButtonTooltip(report) {
            if (this.finalizeButtonDisabled(report)) {
                if (report.amended) {
                    return "An amended report cannot be finalized again.";
                }
                if (!report.finalized) {
                    return "Load the report first. All edits must be saved.";
                }
            }
            else {
                return "Finalize the report. NO OTHER REPORT CAN BE FINALIZED AFTER THIS ONE";
            }
        }

    },
    computed: {

    },
    created() {

    },
    destroyed() {
    },
    mounted() {
    },
    watch: {
    }



});

