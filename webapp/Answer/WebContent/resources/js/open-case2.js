const OpenCase2 = {
    props: {
        "readonly": { default: true, type: Boolean },
        loadingColor: { default: "blue-grey lighten-4", type: String },
        flt3ITDLocus: { default: "chr13:28,033,867-28,034,235", type: String },
        confirmationMessage: { default: "Some changes have not been saved.<br/>Are you sure?", type: String },
        confirmationProceedButton: { default: "Proceed", type: String },
        confirmationCancelButton: { default: "Cancel", type: String },
    },
    template: /*html*/`<div>

    <!-- splash screen dialog -->
    <splash-screen ref="splashScreen" :splash-dialog="splashDialog" ></splash-screen>

    <!-- copy to clipboard dialog -->
    <v-dialog v-model="copyDialogVisible" max-width="50%">
    <v-card>
    <v-card-title class="title primary white--text">
    Link to the IGV Session File
    </v-card-title>
        <v-card-text class="pl-3 pr-3 pt-3 subheading">
        <div class="font-weight-bold pb-3">
            <span>Use the link below in IGV </span>
            <v-icon class="align-bottom">mdi-chevron-right</v-icon>
            <span>File </span>
            <v-icon class="align-bottom">mdi-chevron-right</v-icon>
            <span>Load From URL</span>
        </div>
                <v-text-field 
                ref="copyTextField" 
                :value="textToCopy" 
                @focus="$event.target.select()"
                single-line readonly outline hide-details></v-text-field>
        </v-card-text>
        <v-card-actions>
            <v-tooltip bottom>
                <v-btn class="mr-2" color="primary" slot="activator" @click="copyLink">
                Copy
                </v-btn>
                <span>Copy to clipboard</span>
            </v-tooltip>
            <v-btn color="error" @click="closeCopyDialog">
                Close
            </v-btn>
        </v-card-actions>
    </v-card>
    </v-dialog>

        <!-- add Fusion dialog -->
        <v-dialog v-model="addFusionDialogVisible" max-width="500px" scrollable>
        <add-fusion  @hide-add-fusion-panel="closeAddFusionDialog"
        :no-edit="!canProceed('canAnnotate') || readonly"
      @refresh-fusion-table="getAjaxData"></add-fusion>
      </v-dialog>
 
    <v-dialog v-model="confirmationDialogVisible" max-width="500px">
        <v-card>
            <v-card-text v-html="confirmationMessage" class="pl-2 pr-2 subheading">

            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-btn class="mr-2" color="primary" @click="proceedWithConfirmation" slot="activator">{{ confirmationProceedButton }}
                </v-btn>
                <v-btn class="mr-2" color="error" @click="cancelConfirmation" slot="activator">{{ confirmationCancelButton }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>


    <!-- create ITD dialog -->
    <v-dialog v-model="itdDialogVisible" max-width="300px" v-if="canProceed('canSelect') && !readonly" persistent>
    <create-itd
    @hide-create-itd="itdDialogVisible = false"
    @refresh-variants="getAjaxData"
    @show-snackbar="showSnackBarMessageWithParams"></create-itd>
    </v-dialog>

    <!-- variant details dialog -->
        <variant-details-dialog :colors="colors" ref="variantDetailsDialog" 
        :isSaveNeededOverall="saveAllNeeded"
        :saveTooltip="createSaveTooltip()"
        :caseName="caseConcatName"
        :caseTypeIcon="caseTypeIcon"
        :caseType="caseType"
        :isSaveLoading="waitingForAjaxActive"
        :readonly="readonly"
        :cnvChromList="cnvChromList"
        @show-snackbar="showSnackBarMessage"
        @close-variant-details="closeVariantDetailsExternally"
        :loadingColor="loadingColor"
        @updateEditAnnotationBreadcrumbs="updateEditAnnotationBreadcrumbs"
        @breadcrumb-navigation="breadcrumbNavigation"
        :breadcrumbs="breadcrumbs"
        :variantDetailsVisible=variantDetailsVisible
        :urlQuery="urlQuery"
        @handle-save-all="handleSaveAll"
        @annotation-selection-saved="handleAnnotationSelectionSaved"
        @load-prev-variant="loadPrevVariant"
        @load-next-variant="loadNextVariant"
        @selection-changed="handleSelectionChangeFromVariantDetails"
        
        @download-igv-file="downloadIGVFile"
        @refresh-variant-tables="getAjaxData"
        >
        </variant-details-dialog>

   
    <!-- FPKM plot drawer -->
    <v-menu
        v-model="fpkmVisible"
        :close-on-content-click="false"
        :close-on-click="false"
        :position-x="fpkmPositionx"  :position-y="fpkmPositiony"
        absolute
      >
      <v-toolbar dense dark class="primary">
      <v-toolbar-title>
      Comparing FPKM with Other Cases
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-tooltip bottom>
      <v-btn flat icon @click="closeFPKMChart" slot="activator">
          <v-icon>close</v-icon>
      </v-btn>
      <span>Close FPKM Plot</span>
      </v-tooltip>
      </v-toolbar>
      <fpkm-plot ref="fpkmPlot"
      :can-plot="patientDetailsOncoTreeDiagnosis.text != null"
      :oncotreeCode="patientDetailsOncoTreeDiagnosis.text"
      @hide-fpkm-plot="closeFPKMChart"
      ></fpkm-plot>
      </v-menu>

    <!-- TMB plot drawer -->
    <v-menu
        v-model="tmbVisible"
        :close-on-content-click="false"
        :close-on-click="false"
        :position-x="tmbPositionx"  :position-y="tmbPositiony"
        absolute
      >
      <v-toolbar dense dark class="primary">
      <v-toolbar-title>
      Comparing TMB with Other Cases
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-tooltip bottom>
      <v-btn flat icon @click="closeTMBChart" slot="activator">
          <v-icon>close</v-icon>
      </v-btn>
      <span>Close TMB Plot</span>
      </v-tooltip>
      </v-toolbar>
      <tmb-plot ref="tmbPlot"
      :can-plot="patientDetailsOncoTreeDiagnosis.text != null && hasTMB"
      :oncotreeCode="patientDetailsOncoTreeDiagnosis.text"
      @hide-tmb-plot="closeTMBChart"
      ></tmb-plot>
      </v-menu>  
      
       <!-- Mutational Signature Image drawer -->
       <v-menu
       v-model="mutSigVisible"
       :position-x="mutSigPositionx"  :position-y="54" 
       :close-on-content-click="false"
       :close-on-click="true"
       absolute
     >
     <v-card>
     <v-card-text>
     <v-img v-if="mutationalSignatureImage" alt="Mutational Signature Image" :src="mutationalSignatureImage" :width="1024">
     </v-img>
     <img :src="webAppRoot + '/resources/images/mutational_signature_axis.png'" alt="X Axis" width="1024px" class="mut-sig-image-axis"/>

     <div class="pa-2 centered">
     <table>
        <tr class="font-weight-bold">
            <td>Signature</td><td>Proposed Etiology</td><td>Value</td>
        </tr>
        <tr v-for="(item, index) in mutSigTableData" :key="index">
            <td>{{ item.signature }}</td><td>{{ item.proposedEtiology }}</td><td>{{ item.value }}</td>
        </tr>
     </table>
     <div >
        
     </div>
     </div>
     </v-card-text>
     </v-card>
     </v-menu>  
   
    <v-snackbar :timeout="snackBarTimeout" :bottom="true" right v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-tooltip top>
        <a slot="activator" :href="snackBarLink"><v-icon dark>{{ snackBarLinkIcon }}</v-icon></a>
        <span>Open Link</span>
        </v-tooltip>
        <v-btn flat color="primary" @click="snackBarVisible = false">Close</v-btn>
    </v-snackbar>

    <!-- advanced filter-->
    <advanced-filter ref="advancedFilter" @refresh-data="filterData"
        :type="currentFilterType"
        @update-highlight="updateHighlights"></advanced-filter>

    <!-- review selection dialog -->
    <v-dialog v-model="reviewDialogVisible" scrollable fullscreen hide-overlay persistent transition="dialog-bottom-transition">
        <review-selection ref="reviewDialog"
        :open-report-url="getOpenReportHref()"
        :report-ready="reportReady"
        :breadcrumbs="breadcrumbs"
        :case-name="caseConcatName" :case-type="caseType" :case-type-icon="caseTypeIcon"
        @save-selection="saveSelection" @close-review-dialog="closeReviewDialog"
        :is-save-badge-visible="isSaveNeededBadgeVisible()" :save-variant-disabled="saveVariantDisabled"
        @save-all="handleSaveAll" :waiting-for-ajax-active="waitingForAjaxActive" @show-snackbar="showSnackBarMessageWithParams"
        :save-tooltip="createSaveTooltip()"
        :case-owner-id="caseOwnerId"
        :case-owner-name="caseOwnerName"
        :user-id="userId + ''"
        @review-selection-refresh="updateSelectedVariantTable()"
        :readonly="readonly"
        @accept-selection-from="addOtherAnnotatorSelection"
        @auto-select-vus="autoSelectVUSs"></review-selection>
    </v-dialog>


    <v-toolbar dense dark :color="colors.openCase" fixed app>
    <v-tooltip class="ml-0" bottom>
        <v-menu offset-y offset-x slot="activator" class="ml-0">
            <v-btn slot="activator" flat icon dark>
                <v-icon>more_vert</v-icon>
            </v-btn>
            <v-list>
                <v-list-tile :class="!patientDetailsVisible ? 'grey--text' : ''" avatar @click="patientDetailsVisible = !patientDetailsVisible" :disabled="patientTables.length == 0">
                    <v-list-tile-avatar>
                        <v-icon>assignment_ind</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Show/Hide Patient Details</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile :class="!caseAnnotationsVisible ? 'grey--text' : ''"  avatar @click="caseAnnotationsVisible = !caseAnnotationsVisible" :disabled="patientTables.length == 0">
                    <v-list-tile-avatar>
                        <v-icon>mdi-message-bulleted</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Show/Hide Case Notes</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar v-if="qcUrl" @click="openLink(qcUrl)" :disabled="!qcUrl">
                    <v-list-tile-avatar>
                        QC
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Open QC in NuCLIA</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="openMutSigImage" :disabled="!mutationalSignatureImage">
                <v-list-tile-avatar>
                <v-icon>mdi-chart-histogram</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>
                    <span v-if="mutationalSignatureImage">Open Mutational Signature</span>
                    <span v-else>No Mutational Signature Available</span>
                    </v-list-tile-title>
                </v-list-tile-content>
                </v-list-tile>

                <v-list-tile class="list-menu">
                <v-list-tile-content>
                    <v-list-tile-title>
                        <v-menu offset-y offset-x close-delay="500" open-on-hover>
                                <span slot="activator">
                                <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>Compare with Other Cases
                                </span>
                                <v-list>
                        <v-list-tile avatar @click="openFPKMChart">
                                <v-list-tile-avatar>
                                <img alt="boxplot icon" class="alpha-54" :src="webAppRoot + '/resources/images/boxplot_icon_black.png'" width="36px" />
                            </v-list-tile-avatar>
                                <v-list-tile-content >
                                    <v-list-tile-title>FPKM</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                            <v-list-tile avatar @click="openTMBChart">
                            <v-list-tile-avatar>
                            <img alt="boxplot icon" class="alpha-54" :src="webAppRoot + '/resources/images/boxplot_icon_black.png'" width="36px" />
                        </v-list-tile-avatar>
                            <v-list-tile-content >
                                <v-list-tile-title>TMB</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>    

                        </v-list>
                    </v-menu> 
                </v-list-tile-title>
            </v-list-tile-content>
        </v-list-tile>   

                <v-list-tile class="list-menu">
                        <v-list-tile-content>
                            <v-list-tile-title>
                                <v-menu offset-y offset-x close-delay="500" open-on-hover>
                                        <span slot="activator">
                                            <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>IGV
                                            <!-- This is a hack to extend the menu active area because the title is much shorter than other items -->
                                            <span v-for="i in 30" :key="i">&nbsp;</span>
                                        </span>
                                        <v-list>
                                <v-list-tile avatar @click="openBamViewerLinkWebFLT3()">
                                        <v-list-tile-avatar>
                                            <v-icon>mdi-web</v-icon>
                                        </v-list-tile-avatar>
                                        <v-list-tile-content >
                                            <v-list-tile-title>Open FLT3 locus (web)</v-list-tile-title>
                                        </v-list-tile-content>
                                    </v-list-tile>

                                    <v-list-tile avatar @click="downloadIGVFile('jnlp', flt3ITDLocus)">
                                    <v-list-tile-avatar>
                                        <v-icon>mdi-desktop-mac-dashboard</v-icon>
                                    </v-list-tile-avatar>
                                    <v-list-tile-content >
                                        <v-list-tile-title>Open FLT3 locus (desktop)</v-list-tile-title>
                                    </v-list-tile-content>
                                    </v-list-tile>

                                    <v-list-tile avatar @click="downloadIGVFile('session', flt3ITDLocus)">
                                    <v-list-tile-avatar>
                                        <v-icon>mdi-file-code</v-icon>
                                    </v-list-tile-avatar>
                                    <v-list-tile-content  >
                                        <v-list-tile-title>Download IGV Session for FLT3 locus</v-list-tile-title>
                                    </v-list-tile-content>
                                    </v-list-tile>

                                    <v-list-tile v-if="isSNP()" avatar @click="downloadIGVFile('sessionLink', flt3ITDLocus)">
                                    <v-list-tile-avatar>
                                        IGV
                                    </v-list-tile-avatar>
                                    <v-list-tile-content  class="mb-2">
                                        <v-list-tile-title>Show IGV Session Link <v-icon>mdi-link-variant</v-icon></v-list-tile-title>
                                    </v-list-tile-content>
                                    </v-list-tile>

                                </v-list>
                            </v-menu> 
                        </v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>   

                <v-list-tile avatar @click="openReviewSelectionDialog()">
                    <v-list-tile-avatar>
                        <v-icon>mdi-clipboard-check</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Review Variants Selected</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar :to="getOpenReportHref()" :disabled="!reportReady">
                <v-list-tile-avatar>
                    <v-icon>assignment</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Open Report</v-list-tile-title>
                </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="toggleLookupTool()">
                <v-list-tile-avatar>
                    <v-icon>mdi-dna</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Open Lookup Tool</v-list-tile-title>
                </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="handleSaveAll()" :disabled="!isSaveNeededBadgeVisible()">
                <v-list-tile-avatar>
                    <v-icon>save</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Save Current Work</v-list-tile-title>
                </v-list-tile-content>
            </v-list-tile>
            </v-list>
        </v-menu>
        <span>Case Menu</span>
    </v-tooltip>
    <v-toolbar-title class="white--text ml-0">
    <v-tooltip bottom>
    <span slot="activator">Working on case: {{ caseConcatName }}</span>
    <span>Working on case: {{ caseConcatName }} ({{caseType}})</span>
    </v-tooltip>
    <v-tooltip bottom>
          <v-icon slot="activator" :size="caseTypeIconSize" class="icon-align-cancel"> {{ caseTypeIcon }} </v-icon>
        <span>{{caseType}} case</span>  
        </v-tooltip>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-fade-transition>
        <v-progress-linear class="ml-4 mr-4" v-if="(!caseConcatName || loadingVariantDetails) && !splashDialog"
        :indeterminate="true" color="white" style="max-width:200px"></v-progress-linear>
        </v-fade-transition>    
    <v-tooltip bottom>
        <v-btn :disabled="patientTables.length == 0" flat icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator"
            :color="patientDetailsVisible ? 'amber accent-2' : ''">
            <!-- <v-icon>perm_identity</v-icon> -->
            <v-icon>assignment_ind</v-icon>
        </v-btn>
        <span>Show/Hide Patient Details</span>
    </v-tooltip>
    <v-tooltip bottom>
        <v-btn flat icon @click="caseAnnotationsVisible = !caseAnnotationsVisible" :color="caseAnnotationsVisible ? 'amber accent-2' : ''"
            slot="activator">
            <v-icon>mdi-message-bulleted</v-icon>
        </v-btn>
        <span>Show/Hide Case Notes</span>
    </v-tooltip>
    <v-tooltip bottom>
        <v-btn icon flat slot="activator" :href="qcUrl" target="_blank" rel="noreferrer" :disabled="!qcUrl">
            QC
        </v-btn>
        <span>Open QC in NuCLIA</span>
    </v-tooltip>
    
<v-tooltip bottom>
    <v-btn icon flat slot="activator" @click="openMutSigImage" :disabled="!mutationalSignatureImage">
    <v-icon>mdi-chart-histogram</v-icon>
    </v-btn>
    <span v-if="mutationalSignatureImage">Open Mutational Signature</span>
    <span v-else>No Mutational Signature Available</span>
</v-tooltip>

<v-menu dark class="primary" origin="center center" transition="slide-y-transition" bottom open-on-hover offset-y z-index="9">
<v-tooltip left  slot="activator">
<v-btn icon flat slot="activator">
<img alt="boxplot icon" :src="webAppRoot + '/resources/images/boxplot_icon_white.png'" width="36px" />
</v-btn>
<span>Compare with Other Cases</span>
</v-tooltip>
<v-card color="primary">
<v-list dense class="primary">
    <v-list-tile @click="openFPKMChart">FPKM</v-list-tile>
    <v-list-tile @click="openTMBChart">TMB</v-list-tile>
</v-list>
</v-card>
</v-menu>

<v-menu origin="center center" transition="slide-y-transition" bottom open-on-hover offset-y >
<v-btn icon flat slot="activator">IGV
</v-btn>
<v-card color="primary">
<v-tooltip bottom >
    <v-btn ref="bamViewerLinkFLT3" dark icon flat slot="activator" :href="createBamViewerLinkFLT3()" target="_blank" rel="noreferrer">
        <v-icon>mdi-web</v-icon> 
    </v-btn>
    <span>Open FLT3 locus (web)</span>
</v-tooltip>
<br/>
<v-tooltip bottom >
    <v-btn  dark icon flat slot="activator" @click="downloadIGVFile('jnlp', flt3ITDLocus)">
        <v-icon>mdi-desktop-mac-dashboard</v-icon>
    </v-btn>
    <span>Open FLT3 locus (desktop)</span>
</v-tooltip>
<br/>
<v-tooltip bottom >
    <v-btn dark icon flat slot="activator" @click="downloadIGVFile('session', flt3ITDLocus)">
        <v-icon>mdi-file-code</v-icon>
    </v-btn>
    <span>Download IGV Session for FLT3 locus</span>
</v-tooltip>
<br/>
<v-tooltip bottom >
    <v-btn dark icon flat slot="activator" @click="downloadIGVFile('sessionLink', flt3ITDLocus)">
        <v-icon>mdi-link-variant</v-icon>
    </v-btn>
    <span>Show IGV Session Link for FLT3 locus</span>
</v-tooltip>
</v-card>
</v-menu>

        <v-tooltip bottom>
            <v-btn flat icon @click="openReviewSelectionDialog()" slot="activator" :color="reviewDialogVisible ? 'amber accent-2' : ''">
                <v-icon>mdi-clipboard-check</v-icon>
            </v-btn>
            <span>Review Variants Selected</span>
        </v-tooltip>
    <v-badge color="red" right bottom overlap v-model="isSaveNeededBadgeVisible()" class="mini-badge">
    <v-icon slot="badge"></v-icon>
    <v-tooltip bottom offset-overflow nudge-left="100px" min-width="200px">
        <v-btn flat icon @click="handleSaveAll()" slot="activator" :disabled="!isSaveNeededBadgeVisible()">
            <v-icon>save</v-icon>
        </v-btn>
        <span v-html="createSaveTooltip()"></span>
    </v-tooltip>
    </v-badge>
</v-toolbar>

<v-layout row wrap>
    <v-flex :class="isLookupVisible() ? 'xs8' : 'xs12'">
        

<v-breadcrumbs class="pt-2">
    <v-icon slot="divider">forward</v-icon>
    <v-breadcrumbs-item v-for="(item, index) in breadcrumbs" :key="item.text" :disabled="disableBreadCrumbItem(item, index)"
        @click.native="breadcrumbNavigation(index)">
        {{ item.text }}
    </v-breadcrumbs-item>
</v-breadcrumbs>

<v-slide-y-transition>
    <v-layout v-if="patientDetailsVisible">
        <v-flex :class="isLookupVisible() || isAdvancedFilteringVisible() ? ['xs12','md12','lg12','xl12'] : ['xs12','md12','lg12','xl10']">
            <div class="text-xs-center pb-3">
                <v-card>
                    <v-toolbar class="elevation-0" dense dark :color="colors.openCase">
                        <v-menu offset-y offset-x class="ml-0">
                            <v-btn slot="activator" flat icon dark>
                                <v-icon color="amber accent-2">assignment_ind</v-icon>
                            </v-btn>
                            <v-list>
                                <v-list-tile avatar @click="patientDetailsVisible = false">
                                    <v-list-tile-avatar>
                                        <v-icon>cancel</v-icon>
                                    </v-list-tile-avatar>
                                    <v-list-tile-content>
                                        <v-list-tile-title>Close Patient Details</v-list-tile-title>
                                    </v-list-tile-content>
                                </v-list-tile>
                            </v-list>
                        </v-menu>
                        <v-toolbar-title>Patient Details</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-tooltip bottom>
                            <v-btn flat icon @click="patientDetailsVisible = false" slot="activator">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Close Details</span>
                        </v-tooltip>
                    </v-toolbar>
                    <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
                        <v-layout row wrap>
                            <v-flex xs4 v-for="table in patientTables" :key="table.name">
                                <v-card flat>
                                    <v-card-text>
                                        <v-list class="dense-tiles">
                                            <v-list-tile v-for="item in table.items" :key="item.label" class="pl-0 pr-0 pb-2 no-tile-padding">
                                                <v-list-tile-content :class="[item.field == 'oncotree' ? 'pt-1': '', 'pl-0','pr-0']">
                                                    <v-layout class="full-width " justify-space-between align-start>
                                                        <v-flex :class="getPatientDetailLabelClass(item)">
                                                            <span :class="[item && item.active ? 'primary--text': '', 'selectable']">{{ item.label }}:</span>
                                                        </v-flex>
                                                        
                                                        <v-flex :class="item && item.field == 'icd10' ? 'xs10' : 'xs7'" grow>
                                                            <v-layout row wrap justify-end>
                                                                <v-flex xs6 v-if="item.field == 'tmb'" class="align-flex-right pt-0 pb-1">
                                                                <v-select :items="tmbClassItems" label="Class" v-model="tmbClass" class="hide-details mt-0 pt-0 pb-1"
                                                                :disabled="!canProceed('canAnnotate')  || readonly" hide-details @input="patientDetailsUnSaved = true" 
                                                                @focus="item.active = true" @blur="item.active = false"></v-select>
                                                                </v-flex>
        
                                                                <v-flex xs6 lg3 v-if="item.field == 'msi'" class="align-flex-right  pt-0 pb-1">
                                                                <v-select :items="msiItems" label="Status" class="hide-details mt-0 pt-0 pb-1"
                                                                :disabled="!canProceed('canAnnotate')  || readonly" v-model="msiClass" hide-details @input="patientDetailsUnSaved = true"
                                                                @focus="item.active = true" @blur="item.active = false"></v-select>
                                                                </v-flex>
                                                                
                                                                <v-flex :class="[getPatientDetailsFlexClass(item),'text-xs-right', '', 'blue-grey--text', 'text--lighten-1', 'mt-0']">
                                                                <span v-if="item.type == null" class="selectable">{{ item.value }}</span>
                                                                    <v-tooltip bottom>
                                                                    <v-autocomplete class="pt-0 mt-0 pb-1" slot="activator" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text' && item.field == 'oncotree'" 
                                                                    v-model="patientDetailsOncoTreeDiagnosis" :items="oncotree" single-line return-object
                                                                    item-text="text" item-value="text" hide-details @input="patientDetailsUnSaved = true"
                                                                    @focus="item.active = true" @blur="item.active = false">
                                                                    </v-autocomplete>
                                                                    <span> {{ patientDetailsOncoTreeDiagnosis.label }}</span>
                                                                    </v-tooltip>
                                                                    <v-textarea class="hide- details align-input-right mt-0 pt-0 pb-1" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'tumorTissueType'" v-model="patientDetailsTumorTissue"
                                                                    label="Tumor Tissue Type" single-line @input="patientDetailsUnSaved = true" hide-details rows="1" auto-grow @focus="item.active = true" @blur="item.active = false">
                                                                    </v-textarea>
                                                                    <v-textarea class="hide-details align-input-right mt-0 pt-0 pb-1" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'icd10'" v-model="patientDetailsICD10"
                                                                    label="ICD10 Code" single-line @input="patientDetailsUnSaved = true" hide-details rows="1" auto-grow @focus="item.active = true" @blur="item.active = false">
                                                                    </v-textarea>
                                                                    <v-text-field class="hide-details align-input-right mt-0 pt-0 pb-1" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'dedupPctOver100X'" v-model="patientDetailsDedupPctOver100X"
                                                                    label="Numbers Only" :rules="numberRules" single-line @input="patientDetailsUnSaved = true" hide-details @focus="item.active = true" @blur="item.active = false">
                                                                    </v-text-field>
                                                                    <v-text-field class="hide-details align-input-right mt-0 pt-0 pb-1" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'dedupAvgDepth'" v-model="patientDetailsDedupAvgDepth"
                                                                    label="Numbers Only" :rules="numberRules" single-line @input="patientDetailsUnSaved = true" hide-details @focus="item.active = true" @blur="item.active = false">
                                                                    </v-text-field>
                                                                    <v-text-field class="hide-details align-input-right mt-0 pt-0 pb-1" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'tumorPercent'" v-model="patientDetailsTumorPercent"
                                                                    label="Numbers Only" :rules="numberRules" single-line @input="patientDetailsUnSaved = true" hide-details @focus="item.active = true" @blur="item.active = false">
                                                                    </v-text-field>
                                                                </v-flex>
                                                                <v-flex xs4 v-if="item.type == 'text' && item.field == 'oncotree'" class="align-flex-right pt-0">
                                                                    <v-tooltip bottom>
                                                                        <v-btn flat color="primary" icon :href="getOncoTreeUrl()" target="_blank" rel="noreferrer" slot="activator" class="ma-0">
                                                                            <img alt="oncotree icon" :src="oncotreeIconUrl" width="24px"/>
                                                                        </v-btn>
                                                                        <span>Open OncoTree in New Tab</span>
                                                                    </v-tooltip>
                                                                    <v-tooltip bottom>
                                                                    <v-btn flat color="primary" icon @click="toggleLookupTool" slot="activator" class="ma-0">
                                                                        <v-icon>mdi-dna</v-icon>
                                                                    </v-btn>
                                                                    <span>Open Lookup Tool</span>
                                                                    </v-tooltip>
                                                                </v-flex>
                                                            </v-layout>    
                                                        </v-flex>
                                                       
                                                    </v-layout>
                                                </v-list-tile-content>
                                            </v-list-tile>
                                        </v-list>
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-card>
            </div>
        </v-flex>


    </v-layout>
</v-slide-y-transition>

<v-slide-y-transition>
    <v-layout v-if="caseAnnotationsVisible">
        <v-flex :class="isLookupVisible() ? ['xs12','md12','lg12','xl12', 'pb-3'] : ['xs12','md12','lg12','xl10', 'pb-3']">
            <v-card>
                <v-toolbar class="elevation-0" dense dark :color="colors.openCase">
                    <!-- <v-icon>perm_identity</v-icon> -->
                    <v-icon :color="caseAnnotationsVisible ? 'amber accent-2' : ''">mdi-message-bulleted</v-icon>
                    <v-toolbar-title>Case Notes</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn flat icon @click="caseAnnotationsVisible = false" slot="activator">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Close Annotations</span>
                    </v-tooltip>
                </v-toolbar>
                <v-card-text class="pl-3">
                    <div v-if="labNotes" class="subheading selectable pl-1 pr-2 pt-2 pb-2">
                    <span>Lab Notes:</span>
                    <span class="blue-grey--text text--lighten-1">{{ labNotes }}</span>
                    </div>
                    <v-textarea :readonly="!canProceed('canAnnotate') || readonly" :disabled="!canProceed('canAnnotate') || readonly" hide-details
                        ref="caseNotes" v-model="caseAnnotation.caseAnnotation" @input="caseNotesChanged = true" class="mr-2 no-height" label="Write your comments here">
                    </v-textarea>
                </v-card-text>
            </v-card>
        </v-flex>
    </v-layout>
</v-slide-y-transition>

<v-slide-y-transition>
    <v-tabs slot="extension" dark slider-color="amber accent-2" color="primary darken-1" fixed-tabs 
    v-model="variantTabActive" hide-slider
    :class="variantTabsVisible ? '' : 'hidden'">
        <v-tab href="#tab-snp" :ripple="false" active-class="v-tabs__item--active primary">SNP / Indel</v-tab>
        <v-tab href="#tab-cnv" :ripple="false" active-class="v-tabs__item--active primary">CNV</v-tab>
        <v-tab href="#tab-translocation" :ripple="false" active-class="v-tabs__item--active primary">Fusion / Translocation</v-tab>
        <v-tab href="#tab-virus" :ripple="false" active-class="v-tabs__item--active primary">Virus</v-tab>
        <v-tabs-items>
            <!-- SNP / Indel table -->
            <v-tab-item value="tab-snp">
                <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="SNP/Indel Variants" initial-sort="chromPos"
                    no-data-text="No Data" :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true"
                     :show-left-menu="true"
                    id-type="SNP"
                    @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase" :external-filtering-active="isFilteringActiveForType('snp', 'geneVariantDetails')"
                    >
                    <v-fade-transition slot="action1">
                        <v-tooltip bottom >
                            <v-btn slot="activator" flat icon @click="toggleFilters('snp')" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                                <v-icon>filter_list</v-icon>
                            </v-btn>
                            <span>Advanced Filtering</span>
                        </v-tooltip>
                    </v-fade-transition>
                    <v-list-tile avatar @click="toggleFilters('snp')" slot="action1MenuItem">
                        <v-list-tile-avatar>
                            <v-icon>filter_list</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Advanced Filtering</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                </data-table>
            </v-tab-item>
            <!-- CNV table -->
            <v-tab-item value="tab-cnv">
                <data-table ref="cnvDetails" :fixed="false" :fetch-on-created="false" table-title="CNVs" initial-sort="chrom" no-data-text="No Data"
                    :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true" 
                    :show-left-menu="true" @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase"
                    id-type="CNV"
                    :highlights="highlights" :external-filtering-active="isFilteringActiveForType('cnv', 'cnvDetails')">
                    <v-fade-transition slot="action1">
                        <v-tooltip bottom >
                            <v-btn slot="activator" flat icon @click="toggleFilters('cnv')" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                                <v-icon>filter_list</v-icon>
                            </v-btn>
                            <span>Advanced Filtering</span>
                        </v-tooltip>
                    </v-fade-transition>
                    <v-list-tile avatar @click="toggleFilters('cnv')" slot="action1MenuItem">
                        <v-list-tile-avatar>
                            <v-icon>filter_list</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Advanced Filtering</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                    <v-fade-transition slot="action2">
                    <v-tooltip bottom >
                        <v-btn slot="activator" flat icon @click="getCNVDetailsNoVariant()">
                            <v-icon>zoom_in</v-icon>
                        </v-btn>
                        <span>Open CNV (no variant)</span>
                    </v-tooltip>
                    </v-fade-transition>
                    <v-list-tile avatar @click="getCNVDetailsNoVariant()" slot="action2MenuItem">
                        <v-list-tile-avatar>
                            <v-icon>zoom_in</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Open CNV (no variant)</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                    <v-list-tile avatar @click="openIDTCreationDialog()" slot="action3MenuItem">
                    <v-list-tile-avatar>
                        ITD
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Create a New ITD</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                </data-table>
            </v-tab-item>
            <!--  Fusion / Translocation table -->
            <v-tab-item value="tab-translocation">
                <data-table ref="translocationDetails" :fixed="false" :fetch-on-created="false" table-title="Fusions / Translocations" initial-sort="fusionName"
                    no-data-text="No Data" :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true" 
                    id-type="FTL"
                    :show-left-menu="true" @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase" :external-filtering-active="isFilteringActiveForType('ftl', 'translocationDetails')">
                    <v-fade-transition slot="action1">
                    <v-tooltip bottom >
                        <v-btn slot="activator" flat icon @click="toggleFilters('ftl')" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                            <v-icon>filter_list</v-icon>
                        </v-btn>
                        <span>Advanced Filtering</span>
                    </v-tooltip>
                </v-fade-transition>
                <v-fade-transition slot="action2">
                <v-tooltip bottom >
                    <v-btn slot="activator" flat icon @click="openAddFusionDialog()" :color="addFusionDialogVisible ? 'amber accent-2' : 'white'">
                        <v-icon>mdi-plus-circle-outline</v-icon>
                    </v-btn>
                    <span>Add New Fusion</span>
                </v-tooltip>
            </v-fade-transition>
                <v-list-tile avatar @click="toggleFilters('ftl')" slot="action1MenuItem">
                    <v-list-tile-avatar>
                        <v-icon>filter_list</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Advanced Filtering</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                <v-list-tile avatar @click="openAddFusionDialog()" slot="action2MenuItem">
                    <v-list-tile-avatar>
                        <v-icon>mdi-plus-circle-outline</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Add New Fusion</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                </data-table>
            </v-tab-item>

            <!--  Virus table -->
            <v-tab-item value="tab-virus">
                <data-table ref="virusDetails" :fixed="false" :fetch-on-created="false" table-title="Virus" initial-sort="virusName"
                    no-data-text="No Data" :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true" 
                    id-type="VIR"
                    :show-left-menu="true" @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase" :external-filtering-active="isFilteringActiveForType('virus', 'virusDetails')">
                    <v-fade-transition slot="action1">
                    <v-tooltip bottom >
                        <v-btn slot="activator" flat icon @click="toggleFilters('virus')" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                            <v-icon>filter_list</v-icon>
                        </v-btn>
                        <span>Advanced Filtering</span>
                    </v-tooltip>
                </v-fade-transition>
                <v-list-tile avatar @click="toggleFilters('virus')" slot="action1MenuItem">
                    <v-list-tile-avatar>
                        <v-icon>filter_list</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Advanced Filtering</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                </data-table>
            </v-tab-item>
        </v-tabs-items>
    </v-tabs>
</v-slide-y-transition>
</v-flex>
<v-flex v-show="isLookupVisible()" class="xs4 max-lookup-width">
    <!-- lookup tools-->
<lookup-panel ref="lookupTool" :standalone="false"
:oncotree-items="oncotree"
@reload-values="reloadLookupValues"
></lookup-panel>
</v-flex>
</v-layout>
</div>
`, data() {
        return this.initData();
    }, methods: {
        initData(fromCaseSwitch) {
            return {
                oncotreeIconUrl: oncotreeIconUrl,
                firstTimeLoading: !fromCaseSwitch,
                numberRules: [(v) => { return !isNaN(v) || 'Invalid value' }],
                colors: {
                    openCase: "primary",
                    variantDetails: "primary",
                    saveReview: "primary",
                    editAnnotation: "primary"
                },
                loadingVariantDetails: false,
                patientTables: [],
                qcUrl: "",
                mutationalSignatureUrl: "",
                mutationalSignatureImage: "",
                mutSigVisible: false,
                mutSigTableData: [],
                reportReady: false,
                caseConcatName: "",
                caseId: "",
                caseType: "",
                caseTypeIcon: "",
                caseTypeIconSize: 20,
                patientDetailsVisible: !fromCaseSwitch ? false : this.patientDetailsVisible,
                caseAnnotationsVisible: !fromCaseSwitch ? false : this.caseAnnotationsVisible,
                caseAnnotation: { caseAnnotation: "" },
                caseAnnotationOriginalText: "", //to verify if there has been a modification
                labNotes: null,
                reviewDialogVisible: false,
                caseNotesChanged: false,
                splashDialog: splashDialog,
                breadcrumbs: [],
                variantTabsVisible: fromCaseSwitch,
                variantTabActive: null,
                highlights: {
                    genes: []
                },
                oncotree: [],
                currentFilterType: "snp",
                confirmationDialogVisible: false,
                patientDetailsOncoTreeDiagnosis: {},
                patientDetailsTumorTissue: "",
                patientDetailsICD10: "",
                snackBarVisible: false,
                snackBarLinkIcon: "",
                snackBarLink: "",
                snackBarTimeout: 4000,
                snackBarMessage: "",
                fpkmVisible: false,
                fpkmPositionx: 0,
                fpkmPositiony: 0,
                tmbVisible: false,
                tmbPositionx: 0,
                tmbPositiony: 0,
                mutSigPositionx: 0,
                isVariantOpening: false,
                urlQuery: {
                    showReview: false,
                    variantId: null,
                    variantType: null,
                    edit: false
                },
                variantDetailsVisible: false,
                cnvChromList: [],
                currentVariantType: "snp",
                currentItem: {},
                breadcrumbItemVariantDetails: { text: "Variant Details", disabled: false, params: ["variantId", "variantType"] },
                breadcrumbItemReview: { text: "Review", disabled: false, params: ["showReview"] },
                breadcrumbItemEditAnnotation: { text: "Add / Edit Annotation", disabled: false, params: ["edit"] },
                breadcrumbItemWorkOnCase: { text: "Case Overview", disabled: false, params: [] },
                userId: null,
                saveAllNeeded: false,
                variantUnSaved: false,
                patientDetailsUnSaved: false,
                waitingForAjaxActive: false,
                waitingForAjaxCount: 0,
                waitingForAjaxMessage: "",
                autoSaveInterval: null,
                savingVariantDetails: false,
                savingPatientDetails: false,
                saveVariantDisabled: false,
                caseOwnerId: null,
                caseOwnerName: null,
                updatingSelectedVariantTable: false,
                itdDialogVisible: false,
                copyDialogVisible: false,
                textToCopy: "",
                tmbClassItems: ["High", "Medium", "Low"],
                tmbClass: null,
                msiItems: ["MSI", "MSS"],
                msiClass: null,
                hasTMB: false,
                addFusionDialogVisible: false,
            }
        },
        openIDTCreationDialog() {
            this.itdDialogVisible = true;
        },
        handleRefresh() {
            //issue a warning about unsaved selection
            this.confirmationDialogVisible = true;
        },
        proceedWithConfirmation() {
            this.confirmationDialogVisible = false;
            this.caseChangeCallBack();
            // this.getAjaxData().catch(error => {
            //     this.handleDialogs(error.data, this.proceedWithConfirmation);
            // });;
        },
        cancelConfirmation() {
            this.confirmationDialogVisible = false;
        },
        toggleFilters(type) {
            if (this.$refs.advancedFilter) {
                this.currentFilterType = type;
                this.$refs.advancedFilter.toggleFilters();
            }
        },
        canProceed(field) {
            if (isAdmin) {
                return true;
            }
            switch (field) {
                case "canAnnotate": return permissions.canAnnotate;
                case "canSelect": return permissions.canSelect;
                case "canView": return permissions.canView;
                case "canReview": return permissions.canReview;
                case "canHide": return permissions.canHide;
                default: return false;
            }
        },
        getPatientDetails() {
            axios.get(
                webAppRoot + "/getPatientDetails",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.patientDetailsUnSaved = false;
                        this.patientTables = response.data.patientTables;
                        this.caseConcatName = response.data.caseConcatName;
                        this.extractPatientDetailsInfo();

                    }
                    else {
                        this.handleDialogs(response.data, this.getPatientDetails);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        openFPKMChart() {
            if (this.tmbVisible && this.$refs.tmbPlot) {
                this.fpkmPositionx = this.$refs.tmbPlot.$el.getBoundingClientRect().x - this.$refs.tmbPlot.$el.clientWidth - 10;
            }
            else {
                this.fpkmPositionx = this.$el.clientWidth;
            }
            this.fpkmPositiony = 54;
            this.fpkmVisible = true;
            this.$nextTick(() => {
                setTimeout(() => {
                    if (this.$refs.fpkmPlot)
                        this.$refs.fpkmPlot.loadDefaultFPKMPlot();
                }, 1000);
            });
        },
        closeFPKMChart() {
            this.fpkmVisible = false;
            this.$nextTick(() => {
                if (this.tmbVisible  && this.tmbPositionx < this.fpkmPositionx) {
                    this.tmbPositionx = this.$el.clientWidth;
                }
             });
        },
        openTMBChart() {
            if (this.fpkmVisible && this.$refs.fpkmPlot) {
                this.tmbPositionx = this.$refs.fpkmPlot.$el.getBoundingClientRect().x - this.$refs.fpkmPlot.$el.clientWidth - 10;
            }
            else {
                this.tmbPositionx = this.$el.clientWidth;
            }
            this.tmbPositiony = 54;
            this.tmbVisible = true;
            this.$nextTick(() => {
                setTimeout(() => {
                    if (this.$refs.tmbPlot)
                        this.$refs.tmbPlot.loadDefaultTMBPlot();
                }, 1000);
            });
        },
        closeTMBChart() {
            this.tmbVisible = false;
            this.$nextTick(() => {
                if (this.fpkmVisible && this.fpkmPositionx < this.tmbPositionx) {
                    this.fpkmPositionx = this.$el.clientWidth;
                }
            });
        },
        openMutSigImage(event) {
            if (!this.mutationalSignatureImage) {
                return;
            }
            this.mutSigPositionx = event.clientX;
            this.mutSigVisible = true;
        },
        closeMutSigImage() {
            this.mutSigVisible = false;
        },
        openBamViewerLinkWebFLT3() {
            if (this.$refs.bamViewerLinkFLT3) {
                this.$refs.bamViewerLinkFLT3.$el.click();
            }
        },
        createBamViewerLinkFLT3() {
            var igvRange = "chr13:28,033,867-28,034,235";
            link = "../bamViewer?";
            link += "locus=" + igvRange;
            link += "&caseId=" + this.$route.params.id;
            return link;
        },
        downloadIGVFile(igvType, igvRangeBypass) {
            var igvRange = "";
            if (igvRangeBypass) {
                igvRange = igvRangeBypass;
            }
            else {
                igvRange = this.currentItem.chrom + ":";
                igvRange += this.currentItem.pos - 100;
                igvRange += "-";
                igvRange += this.currentItem.pos + 99;
            }
            axios.get(
                webAppRoot + "/downloadLocalIGVFile",
                {
                    params: {
                        locus: igvRange,
                        caseId: this.$route.params.id,
                        type: igvType
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        if (response.data.payload.sessionType == "sessionLink") {
                            this.textToCopy = response.data.payload.link;
                            this.copyDialogVisible = true;
                        }
                        else {
                            var hiddenElement = document.createElement('a');
                            hiddenElement.href = webAppRoot + "/igv/" + response.data.payload;
                            hiddenElement.download = response.data.payload;
                            document.body.appendChild(hiddenElement);
                            hiddenElement.click();
                            document.body.removeChild(hiddenElement);
                        }
                    }
                    else {
                        this.handleDialogs(response.data, this.downloadIGVFile.bind(null, igvType, igvRangeBypass));
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
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
        isSaveNeededBadgeVisible() {
            if (!this.saveAllNeeded) {
                this.saveAllNeeded = this.annotationSelectionUnSaved
                    || this.variantUnSaved
                    || this.patientDetailsUnSaved
                    || this.variantDetailsUnsaved
                    || this.isCaseAnnotationChanged();
            }
            this.$store.commit("updateOpenCaseSaveNeeded", this.saveAllNeeded);
            return this.saveAllNeeded && !this.readonly;
        },
        handleAnnotationSelectionSaved() {
            this.waitingForAjaxCount--;
        },
        createSaveTooltip() {
            var tooltip = ["Some edits have not been saved yet:"];
            if (this.annotationSelectionUnSaved) {
                tooltip.push("- Annotation Selection");
            }
            if (this.variantUnSaved) {
                tooltip.push("- Variant Selection");
            }
            if (this.patientDetailsUnSaved) {
                tooltip.push("- Patient Details");
            }
            if (this.isCaseAnnotationChanged()) {
                tooltip.push("- Case Notes");
            }
            if (this.variantDetailsUnsaved) {
                tooltip.push("- Variant Details");
            }
            if (tooltip.length > 1) {
                return tooltip.join("<br/>");
            }
            this.saveAllNeeded = false;
            this.$store.commit("updateOpenCaseSaveNeeded", this.saveAllNeeded);
            return "Nothing to Save";
        },
        isCaseAnnotationChanged() {
            return this.caseNotesChanged;
        },
        isFilteringActiveForType(variantType, tableRef) {
            if (this.$refs.advancedFilter && this.$refs[tableRef]) {
                if (this.$refs.advancedFilter.filterNeedsReload) {
                    return this.$refs[tableRef].filteringActive;
                }
                else {
                    return this.$refs.advancedFilter.isAnyFilterUsedByType(variantType);
                }
            }
            return false;
        },
        handleSelectionChangeFromVariantDetails(oid, idType, isSelected) {
            //idType is used to know which table the event came from.
            var payload = {id: oid, selected: isSelected, userId: this.userId};
            if (idType == "SNP") {
                this.$store.commit("snpStore/updateSelectedVariant", payload);
            }
            if (idType == "CNV") {
                this.$store.commit("cnvStore/updateSelectedVariant", payload);
            }
            else if (idType == "FTL") {
                this.$store.commit("ftlStore/updateSelectedVariant", payload);
            }
            else if (idType == "VIR") {
                this.$store.commit("virStore/updateSelectedVariant", payload);;
            }
            this.variantUnSaved = true;
        },
        handleSelectionChanged(selectedSize, item, idType) {
            //idType is used to know which table the event came from.
            var payload = {id: item.oid, selected: item.isSelected, userId: this.userId};
            if (!item.isSelected) {
                delete item.selectionPerAnnotator[this.userId];
                delete item["dateSince" + this.userId];
            }
            if (idType == "SNP") {
                this.$store.commit("snpStore/updateSelectedVariant", payload);
            }
            if (idType == "CNV") {
                this.$store.commit("cnvStore/updateSelectedVariant", payload);
            }
            else if (idType == "FTL") {
                this.$store.commit("ftlStore/updateSelectedVariant", payload);
            }
            else if (idType == "VIR") {
                this.$store.commit("virStore/updateSelectedVariant", payload);;
            }
            this.variantUnSaved = true;

        },
        getOncoTreeUrl() {
            var leaf = "";
            if (this.patientDetailsOncoTreeDiagnosis.text) {
                leaf = "?version=oncotree_2019_03_01&search_term=(" + this.patientDetailsOncoTreeDiagnosis.text + ")";
            }
            return "http://oncotree.mskcc.org/#/home" + leaf;
        },
        isAdvancedFilteringVisible() {
            return this.$refs.advancedFilter && this.$refs.advancedFilter.advancedFilteringVisible;
        },
        updateHighlights(filter) {
            if (filter.fieldName == "cnvGeneName") {
                var items = null;
                if (filter.value) {
                    if (Array.isArray(filter.value)) {
                        items = filter.value;
                    }
                    else {
                        items = filter.value.split(",");
                    }
                    for (var i = 0; i < items.length; i++) {
                        items[i] = items[i].trim();
                    }
                }
                this.highlights.genes = items;
            }
        },
        filterData() {
            this.getAjaxData().catch(error => {
                this.handleDialogs(error.data, this.filterData);
            });
        },
        showSnackBarMessage(message) {
            this.snackBarMessage = message;
            this.snackBarLink = "";
            this.snackBarLinkIcon = "";
            this.snackBarTimeout = 4000;
            this.snackBarVisible = true;
        },
        showSnackBarMessageWithParams(snackBarMessage, snackBarLink, snackBarLinkIcon, snackBarTimeout) {
            this.snackBarMessage = snackBarMessage;
            this.snackBarLink = snackBarLink;
            this.snackBarLinkIcon = snackBarLinkIcon;
            this.snackBarTimeout = snackBarTimeout != null ? snackBarTimeout : 4000;
            this.snackBarVisible = true;
        },
        getAjaxData() {
            if (this.loadingVariantDetails) {
                return new Promise((resolve, reject) => {
                    resolve({ success: true });
                });
            }
            this.loadingVariantDetails = true;
            if (!this.$refs.advancedFilter) {
                return;
            }
            this.$refs.advancedFilter.loading = true;
            this.mutSigTableData = [];
            return new Promise((resolve, reject) => {

                axios({
                    method: 'post',
                    url: webAppRoot + "/getCaseDetails",
                    params: {
                        caseId: this.$route.params.id,
                        readOnly: this.readonly
                    },
                    data: {
                        filters: this.$refs.advancedFilter.filters
                    }
                }).then(response => {
                    if (!this.$refs.advancedFilter) {
                        return;
                    }
                    if (response.data.isAllowed) {
                        if (this.readonly) {
                            bus.$emit("update-status", ["VIEW ONLY MODE"]);
                        }
                        else {
                            bus.$emit("update-status-off");
                        }
                        // this.patientTables = response.data.patientInfo.patientTables;
                        this.caseAssignedTo = response.data.assignedToIds;
                        this.caseType = response.data.type;
                        this.reportReady = response.data.reportReady;
                        this.caseOwnerId = response.data.caseOwnerId;
                        this.$store.commit("snpStore/updateCaseOwnerId", this.caseOwnerId);
                        this.$store.commit("cnvStore/updateCaseOwnerId", this.caseOwnerId);
                        this.$store.commit("ftlStore/updateCaseOwnerId", this.caseOwnerId);
                        this.$store.commit("virStore/updateCaseOwnerId", this.caseOwnerId);
                        this.caseOwnerName = response.data.caseOwnerName;
                        this.mutSigTableData = response.data.mutationalSignatureData;
                        if (this.caseType == "Clinical") {
                            this.caseTypeIcon = "fa-user-md";
                            this.caseTypeIconSize = 20;
                        }
                        else if (this.caseType == "Research" || this.caseType == "ClinicalResearch") {
                            this.caseTypeIcon = "fa-flask";
                            this.caseTypeIconSize = 18;
                        }
                        this.labNotes = response.data.labNotes;
                        // this.extractPatientDetailsInfo(response.data.caseName);
                        this.caseId = response.data.caseId;
                        this.qcUrl = response.data.qcUrl + this.caseId + "?isLimsId=true&primary=true";
                        this.mutationalSignatureUrl = response.data.tumorVcf ? webAppRoot + "/mutationalSignatureViewer?caseId=" + this.caseId : null;
                        this.mutationalSignatureImage = response.data.mutationalSignatureLinkName ? webAppRoot + "/images/" + response.data.mutationalSignatureLinkName : null;
                        
                        var snpIndelVariantSummary = response.data.snpIndelVariantSummary;
                        var cnvSummary = response.data.cnvSummary;
                        var translocationSummary = response.data.translocationSummary;
                        var virusSummary = response.data.virusSummary;
                        this.addCustomWarningFlags(snpIndelVariantSummary);
                        this.addOtherAnnotatorsValues(snpIndelVariantSummary);
                        this.addOtherAnnotatorsValues(cnvSummary);
                        this.addCNVHeaderAction(cnvSummary.headers);
                        this.addSNPIndelHeaderAction(snpIndelVariantSummary.headers);
                        this.addOtherAnnotatorsValues(translocationSummary);
                        this.addFusionHeaderAction(translocationSummary.headers);
                        this.addOtherAnnotatorsValues(virusSummary);
                        this.addVirusHeaderAction(virusSummary.headers);
                      
                        
                        this.$store.commit("snpStore/updateAllVariantSummary", snpIndelVariantSummary);
                        if (this.$refs.geneVariantDetails)
                            this.$refs.geneVariantDetails.manualDataFilteredFromStore(this.$store.getters["snpStore/getCurrentVariantSummary"], this.$store.getters["snpStore/getCurrentVariantSummaryItems"]);

                        this.$store.commit("cnvStore/updateAllVariantSummary", cnvSummary);
                        if (this.$refs.cnvDetails)
                            this.$refs.cnvDetails.manualDataFilteredFromStore(this.$store.getters["cnvStore/getCurrentVariantSummary"], this.$store.getters["cnvStore/getCurrentVariantSummaryItems"]); 
                        
                        this.$store.commit("ftlStore/updateAllVariantSummary", translocationSummary);
                        if (this.$refs.translocationDetails)
                            this.$refs.translocationDetails.manualDataFilteredFromStore(this.$store.getters["ftlStore/getCurrentVariantSummary"], this.$store.getters["ftlStore/getCurrentVariantSummaryItems"]); 
                        
                        this.$store.commit("virStore/updateAllVariantSummary", virusSummary);
                        if (this.$refs.virusDetails)
                            this.$refs.virusDetails.manualDataFilteredFromStore(this.$store.getters["virStore/getCurrentVariantSummary"], this.$store.getters["virStore/getCurrentVariantSummaryItems"]); 


                        this.$refs.advancedFilter.effects = response.data.effects;
                        this.$refs.advancedFilter.diseaseDatabaseFilters = response.data.diseaseDatabaseFilters;
                        this.$refs.advancedFilter.troubledRegionFilters = response.data.troubledRegionFilters;
                        this.$refs.advancedFilter.failedFilters = response.data.failedFilters;
                        //populate ftlFilters
                        this.$refs.advancedFilter.ftlFilters = response.data.ftlFilters;
                        this.$refs.advancedFilter.checkBoxLabelsByValue = response.data.checkBoxLabelsByValue;
                        this.$refs.advancedFilter.checkBoxFTLLabelsByValue = response.data.checkBoxFTLLabelsByValue;
                        this.$refs.advancedFilter.checkBoxDiseaseDatabaseLabelsByValue = response.data.checkBoxDiseaseDatabaseLabelsByValue;
                        this.$refs.advancedFilter.checkBoxTroubledRegionLabelsByValue = response.data.checkBoxTroubledRegionLabelsByValue;
                        this.userId = response.data.userId;
                        this.$refs.advancedFilter.populateCheckBoxes();

                        this.$refs.advancedFilter.filterNeedsReload = false;

                        this.reportGroups = response.data.reportGroups;
                        if (this.$refs.reviewDialog) {
                            this.$refs.reviewDialog.requiredReportGroups = this.reportGroups.filter(r => r.required);
                        }
                        this.$refs.advancedFilter.reportGroups = this.reportGroups;

                        this.removeCurrentUserSelectionColumnFromHeaders(snpIndelVariantSummary.headerOrder, cnvSummary.headerOrder, translocationSummary.headerOrder
                            , response.data.virusSummary.headerOrder);
                        if (this.$refs.variantDetailsDialog)
                            this.$refs.variantDetailsDialog.setSelected();

                        //TODo might need to handle currentRow

                        //TOOD first time page load versus keeping user's prefs
                        if (this.firstTimeLoading) {
                            this.firstTimeLoading = false;
                            this.patientDetailsVisible = true;
                            this.caseAnnotationsVisible = true;
                        }
                        this.variantTabsVisible = true;
                        this.loadingVariantDetails = false;
                        this.$refs.advancedFilter.loading = false;
                        this.isVariantOpening = false;
                        resolve({
                            success: true
                        });
                    }
                    else {
                        this.loadingVariantDetails = false;
                        this.isVariantOpening = false;
                        reject(response);
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    this.isVariantOpening = false;
                    if (this.$refs.advancedFilter) {
                        this.$refs.advancedFilter.loading = false;
                    }
                    this.handleAxiosError(error);
                }
                );
            })
        },
        removeCurrentUserSelectionColumnFromHeaders(snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder, virSummaryHeaderOrder) {
            var headerOrders = [snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder, virSummaryHeaderOrder];
            for (var i = 0; i < headerOrders.length; i++) {
                for (var j = 0; j < headerOrders[i].length; j++) {
                    if (headerOrders[i][j] == "dateSince" + this.userId) {
                        headerOrders[i].splice(j, 1);
                        break;
                    }
                }
            }
        },
        addCurrentUserSelectionColumnToHeaders(snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder, virSummaryHeaderOrder) {
            var headerOrders = [snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder, virSummaryHeaderOrder];
            for (var i = 0; i < headerOrders.length; i++) {
                headerOrders[i].splice(0, 0, "dateSince" + this.userId);
            }
        },
        addSNPIndelHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "chromPos") {
                    headers[i].itemAction = this.openVariant;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Variant Details";
                }
                if (headers[i].value == "notation" && headers[i]["notationTooltipText"]) {
                    headers[i].itemAction = this.openVariant;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Variant Details";
                }
            }
        },
        addCNVHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "chrom") {
                    headers[i].itemAction = this.openCNV;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "CNV Details";
                    break;
                }
            }
        },
        addFusionHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "fusionName") {
                    headers[i].itemAction = this.openTranslocation;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Translocation Details";
                    break;
                }
            }
        },
        addVirusHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "virusName") {
                    headers[i].itemAction = this.openVirus;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Virus Details";
                    break;
                }
            }
        },
        updateRoute() {
            if (!this.urlQuery.variantId && !this.urlQuery.variantType
                && !this.urlQuery.showReview && !this.urlQuery.edit
                && JSON.stringify(router.currentRoute.query) == "{}") {
                //no params is the same as all params are false/null
                //ignore this step from the route history
                return;
            }
            router.push({ query: this.urlQuery });
        },
        openVariant(item) {
            if (this.isVariantOpening || !this.$refs.variantDetailsDialog) {
                return;
            }
            this.isVariantOpening = true;
            this.urlQuery.variantType = "snp";
            this.urlQuery.variantId = item.oid;
            // item.loading = true;
            this.currentItem = item;
            this.$refs.variantDetailsDialog.setSelected();
            this.updateRoute();
        },
        openCNV(item) {
            if (this.isVariantOpening) {
                return;
            }
            this.urlQuery.variantType = "cnv";
            this.urlQuery.variantId = item.oid;
            this.currentItem = item;
            // item.loading = true;
            this.updateRoute();
        },
        openTranslocation(item) {
            if (this.isVariantOpening) {
                return;
            }
            this.urlQuery.variantType = "translocation";
            this.urlQuery.variantId = item.oid;
            this.currentItem = item;
            // item.loading = true;
            this.updateRoute();
        },
        openVirus(item) {
            if (this.isVariantOpening) {
                return;
            }
            this.urlQuery.variantType = "virus";
            this.urlQuery.variantId = item.oid;
            this.currentItem = item;
            // item.loading = true;
            this.updateRoute();
        },
        extractPatientDetailsInfo() {
            for (var i = 0; i < this.patientTables.length; i++) {
                for (var j = 0; j < this.patientTables[i].items.length; j++) {
                    var item = this.patientTables[i].items[j];
                    if (item.field == "oncotree") {
                        this.patientDetailsOncoTreeDiagnosis = { text: item.value, label: "" };
                    }
                    else if (item.field == "tumorTissueType") {
                        this.patientDetailsTumorTissue = item.value;
                    }
                    else if (item.field == "icd10") {
                        this.patientDetailsICD10 = item.value;
                    }
                    else if (item.field == "dedupPctOver100X") {
                        this.patientDetailsDedupPctOver100X = item.value;
                    }
                    else if (item.field == "dedupAvgDepth") {
                        this.patientDetailsDedupAvgDepth = item.value;
                    }
                    else if (item.field == "tumorPercent") {
                        this.patientDetailsTumorPercent = item.value;
                    }
                    else if (item.field == "tmb") {
                        this.hasTMB = item.value != null;
                        this.tmbClass = item.value2;
                    }
                    else if (item.field == "msi") {
                        this.msiClass = item.value2;
                    }
                }
            }
            this.populateOncotreeLabel(); //update the label
        },
        openReviewSelectionDialog() {
            this.reviewDialogVisible = true;
            this.urlQuery.showReview = true;
            this.updateRoute();
            this.updateSelectedVariantTable();
        },
        openReviewSelectionDialogFromNavigation() {
            this.reviewDialogVisible = true;
            this.urlQuery.showReview = true;
        },
        closeReviewDialog() {
            this.reviewDialogVisible = false;
            this.urlQuery.showReview = false;
            this.updateRoute();
        },
        collectOncoTreeDiagnosis() {
            this.oncotree = oncotree;
            this.populateOncotreeLabel();
        },
        populateOncotreeLabel() {
            for (var i = 0; i < this.oncotree.length; i++) {
                if (this.oncotree[i].text == this.patientDetailsOncoTreeDiagnosis.text) {
                    this.patientDetailsOncoTreeDiagnosis.label = this.oncotree[i].label;
                    break;
                }
            }
        },
        saveCaseAnnotations(skipSnackBar) {
            axios({
                method: 'post',
                url: webAppRoot + "/saveCaseAnnotations",
                params: {
                    caseId: this.$route.params.id,
                    skipSnackBar: skipSnackBar
                },
                data: {
                    annotation: [this.caseAnnotation]
                }
            }).then(response => {
                this.waitingForAjaxCount--;
                if (response.data.isAllowed) {
                    this.loadCaseAnnotations();
                    if (!response.data.skipSnackBar) {
                        this.showSnackBarMessage("Annotation Saved");
                    }
                    this.caseAnnotationOriginalText = this.caseAnnotation.caseAnnotation; //to reset the isCaseAnnotationChanged
                    this.caseNotesChanged = false;
                }
                else {
                    this.handleDialogs(response.data, this.saveCaseAnnotations.bind(null, response.data.skipSnackBar));
                }
            }).catch(error => {
                this.handleAxiosError(error);
            });
        },
        loadCaseAnnotations() {
            axios.get(
                webAppRoot + "/loadCaseAnnotations",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.caseAnnotation = response.data;
                        this.caseAnnotationOriginalText = response.data.caseAnnotation;
                    }
                    else {
                        this.handleDialogs(response, this.loadCaseAnnotations);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        mountComponent() {
            this.snackBarMessage = this.readonly ? "View Only Mode: some actions have been disabled" : "",
                this.snackBarLink = "";
            this.snackBarVisible = this.readonly;

            this.collectOncoTreeDiagnosis();
            this.getPatientDetails();
            this.getAjaxData().then(response => {
                if (this.urlQuery.showReview === true) {
                    this.$nextTick(this.openReviewSelectionDialog());
                }
                this.closeSplashScreen();
            }).catch(error => {
                this.handleDialogs(error.data, this.mountComponent);
            });
            if (this.$refs.advancedFilter) {
                this.$refs.advancedFilter.loadUserFilterSets();
                this.$refs.advancedFilter.getVariantFilters();
            }
            bus.$emit("clear-item-selected", [this]);
            this.loadCaseAnnotations();

            this.$refs.splashScreen.manageSplashScreen();
            this.getCNVChromList();
        },
        getCNVChromList() {
            axios.get(
                webAppRoot + "/getCNVChromList",
                {
                    params: {
                        caseId: this.$route.params.id

                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.cnvChromList = response.data.items;

                    } else {
                        this.handleDialogs(response.data, this.getCNVChromList);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        addCustomWarningFlags(snpIndelVariantSummary) {
            for (var i = 0; i < snpIndelVariantSummary.items.length; i++) {
                var item = snpIndelVariantSummary.items[i];
                this.addCustomWarningFlagsForItem(item);
            }
        },
        addCustomWarningFlagsForItem(item) {
            item.iconFlags.iconFlags.forEach(f => { f.chip = false; });
            var iconFlags = item.iconFlags.iconFlags;
            var warnings = [];
            var tooltips = [];
            if (item.common) {
                warnings.push("C");
                tooltips.push("Common");
            }
            if (item.callsetInconsistent) {
                warnings.push("I");
                tooltips.push("Inconsistent calls");
            }
            if (item.lowComplexity) {
                warnings.push("LCR");
                tooltips.push("Low Complexity Region");
            }
            if (item.likelyArtifact) {
                warnings.push("A");
                tooltips.push("Likely Artifact");
            }
            if (item.isRepeat) {
                warnings.push("R");
                if (item.repeatTypes) {
                    tooltips.push("Repeat Types: " + item.repeatTypes.join(" "));
                }
                else {
                    tooltips.push("Repeats");
                }
            }
            if (warnings.length > 0) {
                iconFlags.push({
                    chip: true,
                    color: "warning",
                    iconName: warnings.join(),
                    tooltip: tooltips.join(", ")
                });
            }
        },
        addOtherAnnotatorsValues(itemSummary) {
            var headers = itemSummary.headers.filter(h => h.map);
            for (var i = 0; i < itemSummary.items.length; i++) {
                var item = itemSummary.items[i];
                var annotatorSelections = item.selectionPerAnnotator;
                for (var j = 0; j < headers.length; j++) {
                    var header = headers[j];
                    if (annotatorSelections[header.mapTo]) {
                        item[header.value] = annotatorSelections[header.mapTo].selectedSince;
                    }
                }
            }
        },
        caseChangeCallBack() {
            Object.assign(this.$data, this.initData(true));
            this.mountComponent();
        },
        handleRouteChanged(newRoute, oldRoute) {
            if (newRoute.path != oldRoute.path) { //prevent reloading data if only changing the query router.push({query: {test:"hello3"}})
                    this.caseChangeCallBack();
            }
            else { //look at the query
                // console.log(newRoute.query, oldRoute.query);
                var newRouteQuery = JSON.stringify(newRoute.query);
                var oldRouteQuery = JSON.stringify(oldRoute.query);
                if (newRouteQuery != oldRouteQuery) { //some params changed
                    this.loadFromParams(newRoute.query, oldRoute.query);
                }
            }
            this.updateBreadcrumbs();
        },
        updateBreadcrumbs() {
            //build the breadcrumb trail
            this.breadcrumbs = [];
            this.breadcrumbs.push(this.breadcrumbItemWorkOnCase);
            if (this.urlQuery.showReview === true) {
                this.breadcrumbs.push(this.breadcrumbItemReview);
            }
            if (this.urlQuery.variantId && this.urlQuery.variantType) {
                this.breadcrumbs.push(this.breadcrumbItemVariantDetails);
            }
            if (this.urlQuery.edit === true) {
                this.breadcrumbs.push(this.breadcrumbItemEditAnnotation);
            }
        },
        loadFromParams(newRouteQuery, oldRouteQuery) {
            this.urlQuery.variantId = this.$route.query.variantId ? this.$route.query.variantId : null;
            this.urlQuery.variantType = this.$route.query.variantType ? this.$route.query.variantType : null;
            this.urlQuery.showReview = this.$route.query.showReview === true || this.$route.query.showReview === "true";
            this.urlQuery.edit = this.$route.query.edit === true || this.$route.query.edit === "true";

            if (!this.urlQuery.variantId) { //close variant details
                this.closeVariantDetails();
                this.toggleHTMLOverlay();
            }
            if (this.urlQuery.variantType) {
                this.variantTabActive = "tab-" + this.urlQuery.variantType;
            }
            if (!this.urlQuery.showReview) {
                this.closeReviewDialog();
            }
            else {
                this.openReviewSelectionDialogFromNavigation();
            }
            //then open variant details
            if (this.urlQuery.variantId && this.urlQuery.variantType) {
                if (this.$route.query.variantId != 'notreal') {
                    var variantFound = false;
                    //find item
                    if (this.urlQuery.variantType == 'snp') {
                        if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                            || !newRouteQuery) {
                            variantFound = true;
                            this.getVariantDetails(this.urlQuery.variantId)
                                .then((response) => {
                                    if (response.success) {
                                        if (this.$refs.variantDetailsDialog) {
                                            this.$refs.variantDetailsDialog.setSelected();
                                            this.$refs.variantDetailsDialog.handleAnnotationSelectionChanged(true);
                                            this.isVariantOpening = false;
                                            //open other dialogs if needed
                                            if (this.urlQuery.edit) {
                                                this.$refs.variantDetailsDialog.startUserAnnotations();
                                            }
                                        }
                                    }
                                })
                                .catch(response => {
                                    this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                                        this.urlQuery.variantId));
                                });
                        }
                    }
                    else if (this.urlQuery.variantType == 'cnv') {
                        if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                            || !newRouteQuery) {
                            variantFound = true;
                            this.getCNVDetails(this.urlQuery.variantId)
                                .then((response) => {
                                    if (response.success) {
                                        if (this.$refs.variantDetailsDialog) {
                                            this.$refs.variantDetailsDialog.setSelected();
                                            this.$refs.variantDetailsDialog.handleAnnotationSelectionChanged(true);
                                            this.isVariantOpening = false;
                                            //open other dialogs if needed
                                            if (this.urlQuery.edit) {
                                                this.$refs.variantDetailsDialog.startUserAnnotations();
                                            }
                                        }
                                    }
                                });
                        }
                    }
                    else if (this.urlQuery.variantType == 'translocation') {
                        if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                            || !newRouteQuery) {
                            variantFound = true;
                            this.getTranslocationDetails(this.urlQuery.variantId)
                                .then((response) => {
                                    if (response.success) {
                                        if (this.$refs.variantDetailsDialog) {
                                            this.$refs.variantDetailsDialog.setSelected();
                                            this.$refs.variantDetailsDialog.handleAnnotationSelectionChanged(true);
                                            this.isVariantOpening = false;
                                            //open other dialogs if needed
                                            if (this.urlQuery.edit) {
                                                this.$refs.variantDetailsDialog.startUserAnnotations();
                                                this.closeSplashScreen();
                                            }
                                        }
                                    }
                                });
                        }
                    }
                    else if (this.urlQuery.variantType == 'virus') {
                        if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                            || !newRouteQuery) {
                            variantFound = true;
                            this.getVirusDetails(this.urlQuery.variantId)
                                .then((response) => {
                                    if (response.success) {
                                        if (this.$refs.variantDetailsDialog) {
                                            this.$refs.variantDetailsDialog.setSelected();
                                            this.$refs.variantDetailsDialog.handleAnnotationSelectionChanged(true);
                                            this.isVariantOpening = false;
                                            //open other dialogs if needed
                                            if (this.urlQuery.edit) {
                                                this.$refs.variantDetailsDialog.startUserAnnotations();
                                                this.closeSplashScreen();
                                            }
                                        }
                                    }
                                });
                        }
                    }
                    if (!variantFound) {
                        this.toggleHTMLOverlay();
                    }
                }
                else {
                    this.getCNVDetailsNoVariant();
                }


            }

        },
        getCNVDetailsNoVariant() {
            this.$refs.variantDetailsDialog.getCNVDetailsNoVariant()
                .then((response) => {
                    if (response.success) {
                        this.isVariantOpening = false;
                        this.variantDetailsVisible = true;
                        this.updateRoute();
                        this.variantDetailsEndedLoading();
                        this.closeSplashScreen();
                    }
                });
        },
        toggleHTMLOverlay() {
            var html = document.querySelector("html");
            if (this.urlQuery.variantId || this.urlQuery.showReview) {
                html.style.overflow = "hidden";
            }
            else {
                html.style.overflow = "";
            }
        },
        isSNP() {
            return this.currentVariantType == "snp";
        },
        isCNV() {
            return this.currentVariantType == "cnv";
        },
        isSNPCNV() {
            return this.isSNP() || this.isCNV();
        },
        isTranslocation() {
            return this.currentVariantType == "translocation";
        },
        isVirus() {
            return this.currentVariantType == "virus";
        },
        closeSplashScreen() {
            setTimeout(() => {
                //make sure the dialogs are stacked up properly
                if (this.urlQuery.showReview && this.urlQuery.variantId && this.$refs.reviewDialog) {
                    let styleVariantDetails = document.getElementsByClassName("variantDetailsDialog")[0].parentElement.style;
                    var styleShowReview = this.$refs.reviewDialog.$parent.$parent.$children[0].$el.parentElement.style;
                    var zIndexVariantDetails = styleVariantDetails.zIndex == "" ? 200 : parseInt(styleVariantDetails.zIndex);
                    var zIndexShowReview = parseInt(styleShowReview.zIndex);
                    while (zIndexVariantDetails <= zIndexShowReview) {
                        zIndexVariantDetails++;
                    }
                    styleVariantDetails.zIndex = zIndexVariantDetails + "";
                }
                this.$nextTick(() => {
                    this.splashDialog = false;
                    splashDialog = false; //disable from now on
                    if (splashInterval) {
                        clearInterval(splashInterval);
                    }
                });
            }, 500);

        },
        getVariantDetails(variantId) {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "snp";
                var table; //could be the selected variant table or the regular one
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog) {
                        table = this.$refs.reviewDialog.getSnpTable();
                    }
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                let currentIndex = -1;
                let isFirstVariant = true;
                let isLastVariant = true;
                if (!this.splashDialog) { //don't allow prev/next when loading from a direct link
                    currentIndex = table.getCurrentItemIndex(this.urlQuery.variantId);
                    isFirstVariant = table.isFirstItem(currentIndex);
                    isLastVariant = table.isLastItem(currentIndex);
                }
                if (this.$refs.variantDetailsDialog) {
                    this.$refs.variantDetailsDialog.getVariantDetails(variantId, isFirstVariant, isLastVariant)
                        .then(response => {
                            if (response.success) {
                                //open the variant details dialog
                                this.variantDetailsVisible = true;
                                this.variantDetailsEndedLoading();
                                resolve({ success: true });
                            }
                        })
                        .catch(response => {
                            this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                                this.urlQuery.variantId));
                        });
                }

            });

        },
        getCNVDetails(variantId) {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "cnv";
                var table; //could be the selected variant table or the regular one
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getCnvTable();
                }
                else {
                    table = this.$refs.cnvDetails;
                }
                let currentIndex = -1;
                let isFirstVariant = true;
                let isLastVariant = true;
                if (!this.splashDialog) { //don't allow prev/next when loading from a direct link
                    currentIndex = table.getCurrentItemIndex(this.urlQuery.variantId);
                    isFirstVariant = table.isFirstItem(currentIndex);
                    isLastVariant = table.isLastItem(currentIndex);
                }
                if (this.$refs.variantDetailsDialog) {
                    this.$refs.variantDetailsDialog.getCNVDetails(variantId, isFirstVariant, isLastVariant)
                        .then(response => {
                            if (response.success) {
                                //open the variant details dialog
                                this.variantDetailsVisible = true;
                                this.variantDetailsEndedLoading();
                                resolve({ success: true });
                            }
                        })
                        .catch(response => {
                            this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                                this.urlQuery.variantId));
                        });
                }

            });

        },
        getTranslocationDetails(variantId) {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "translocation";
                var table; //could be the selected variant table or the regular one
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                let currentIndex = -1;
                let isFirstVariant = true;
                let isLastVariant = true;
                if (!this.splashDialog) { //don't allow prev/next when loading from a direct link
                    currentIndex = table.getCurrentItemIndex(this.urlQuery.variantId);
                    isFirstVariant = table.isFirstItem(currentIndex);
                    isLastVariant = table.isLastItem(currentIndex);
                }
                if (this.$refs.variantDetailsDialog) {
                    this.$refs.variantDetailsDialog.getTranslocationDetails(variantId, isFirstVariant, isLastVariant)
                        .then(response => {
                            if (response.success) {
                                //open the variant details dialog
                                this.variantDetailsVisible = true;
                                this.variantDetailsEndedLoading();
                                resolve({ success: true });
                            }
                        })
                        .catch(response => {
                            this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                                this.urlQuery.variantId));
                        });
                }
            });

        },
        getVirusDetails(variantId) {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "virus";
                var table; //could be the selected variant table or the regular one
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getVirusTable();
                }
                else {
                    table = this.$refs.virusDetails;
                }
                let currentIndex = -1;
                let isFirstVariant = true;
                let isLastVariant = true;
                if (!this.splashDialog) { //don't allow prev/next when loading from a direct link
                    currentIndex = table.getCurrentItemIndex(this.urlQuery.variantId);
                    isFirstVariant = table.isFirstItem(currentIndex);
                    isLastVariant = table.isLastItem(currentIndex);
                }
                if (this.$refs.variantDetailsDialog) {
                    this.$refs.variantDetailsDialog.getVirusDetails(variantId, isFirstVariant, isLastVariant)
                        .then(response => {
                            if (response.success) {
                                //open the variant details dialog
                                this.variantDetailsVisible = true;
                                this.variantDetailsEndedLoading();
                                resolve({ success: true });
                            }
                        })
                        .catch(response => {
                            this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                                this.urlQuery.variantId));
                        });
                }

            });

        },
        loadPrevVariant() {
            var table; //could be the selected table or the regular one
            if (this.isSNP()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getSnpTable();
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                var prevVariant = table.getPreviousItem(this.urlQuery.variantId, true);
                if (prevVariant) {
                    this.openVariant(prevVariant);
                }
            }
            else if (this.isCNV()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getCnvTable();
                }
                else {
                    table = this.$refs.cnvDetails;
                }
                var prevVariant = table.getPreviousItem(this.urlQuery.variantId, true);
                if (prevVariant) {
                    this.openCNV(prevVariant);
                }
            }
            else if (this.isTranslocation()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                var prevVariant = table.getPreviousItem(this.urlQuery.variantId, true);
                if (prevVariant) {
                    this.openTranslocation(prevVariant);
                }
            }
            else if (this.isVirus()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getVirTable();
                }
                else {
                    table = this.$refs.virusDetails;
                }
                var prevVariant = table.getPreviousItem(this.urlQuery.variantId, true);
                if (prevVariant) {
                    this.openVirus(prevVariant);
                }
            }
        },
        isSelectionPerAnnotatorReal(selectionPerAnnotator) {
            //check if it's a real object or a html string (temp v-chip)
            return selectionPerAnnotator && selectionPerAnnotator.userId;
        },
        createTempSelectionPerAnnotator() {
            return "<span tabindex='-1' class='v-chip v-chip--disabled v-chip--label warning v-chip--small white--text'><span class='v-chip__content'><i aria-hidden='true' class='icon material-icons mdi mdi-checkbox-marked' style='font-size: 16px; vertical-align: bottom'></i><span class='pl-2'>latest</span></span></span>";
        },
        updateSelectedVariantTable() {
            if (this.updatingSelectedVariantTable) {
                return;
            }
            this.updatingSelectedVariantTable = true;
            if (this.$refs.reviewDialog)
                this.$refs.reviewDialog.startLoading();

            this.saveVariantDisabled = ( this.$store.getters["snpStore/getSelectedVariantItems"].length == 0 
            && this.$store.getters["cnvStore/getSelectedVariantItems"].length == 0 
            && this.$store.getters["ftlStore/getSelectedVariantItems"].length == 0
                && this.$store.getters["virStore/getSelectedVariantItems"].length == 0) 
                || !this.canProceed('canAnnotate') || this.readonly;


            //add the current user column headerOrder but only to the all annotator table
            if (this.$refs.geneVariantDetails)
                snpAllHeaderOrder = this.$refs.geneVariantDetails.headerOrder.slice();
            if (this.$refs.cnvDetails)
                cnvAllHeaderOrder = this.$refs.cnvDetails.headerOrder.slice();
            if (this.$refs.translocationDetails)
                ftlAllHeaderOrder = this.$refs.translocationDetails.headerOrder.slice();
            if (this.$refs.virusDetails)
                virAllHeaderOrder = this.$refs.virusDetails.headerOrder.slice();

            this.addCurrentUserSelectionColumnToHeaders(snpAllHeaderOrder, cnvAllHeaderOrder, ftlAllHeaderOrder, virAllHeaderOrder);

            if (this.$refs.reviewDialog && this.$refs.geneVariantDetails
                && this.$refs.cnvDetails && this.$refs.translocationDetails
                && this.$refs.virusDetails) {
                    this.$refs.reviewDialog.updateSelectedVariantTable(
                        this.$refs.geneVariantDetails.headers, snpAllHeaderOrder, this.$refs.geneVariantDetails.headerOrder,
                        this.$refs.cnvDetails.headers, cnvAllHeaderOrder, this.$refs.cnvDetails.headerOrder,
                        this.$refs.translocationDetails.headers, ftlAllHeaderOrder, this.$refs.translocationDetails.headerOrder,
                        this.$refs.virusDetails.headers, virAllHeaderOrder, this.$refs.virusDetails.headerOrder);
                }
            this.updatingSelectedVariantTable = false;
        },
        loadNextVariant() {
            var table; //could be the selected table or the regular one
            if (this.isSNP()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getSnpTable();
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                var nextVariant = table.getNextItem(this.urlQuery.variantId, true);
                if (nextVariant) {
                    this.openVariant(nextVariant);
                }
            }
            else if (this.isCNV()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getCnvTable();
                }
                else {
                    table = this.$refs.cnvDetails;
                }
                var nextVariant = table.getNextItem(this.urlQuery.variantId, true);
                if (nextVariant) {
                    this.openCNV(nextVariant);
                }
            }
            else if (this.isTranslocation()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                var nextVariant = table.getNextItem(this.urlQuery.variantId, true);
                if (nextVariant) {
                    this.openTranslocation(nextVariant);
                }
            }
            else if (this.isVirus()) {
                if (this.reviewDialogVisible) {
                    if (this.$refs.reviewDialog)
                        table = this.$refs.reviewDialog.getVirTable();
                }
                else {
                    table = this.$refs.virusDetails;
                }
                var nextVariant = table.getNextItem(this.urlQuery.variantId, true);
                if (nextVariant) {
                    this.openVirus(nextVariant);
                }
            }
        },
        closeVariantDetailsExternally(skipSave) {
            this.closeVariantDetails();
            this.updateRoute();
        },
        closeVariantDetails() {
            if (this.urlQuery && this.$refs.variantDetailsDialog) {
                this.isVariantOpening = false;
                this.variantDetailsVisible = false;
                this.urlQuery.variantId = null;
                this.urlQuery.variantType = null;
                this.urlQuery.edit = false; //also close edit but it should have been done earlier
                this.$refs.variantDetailsDialog.resetCNVChart();
            }
        },
        handleTabChanged(newValue, oldValue) {
            //SNP/Indel tab need to be active to allow filtering
            if (!this.$refs.advancedFilter) {
                return;
            }
            if (this.variantTabActive == "tab-snp") {
                this.currentFilterType = "snp";
                this.$refs.advancedFilter.disableFiltering = false;
                this.$refs.advancedFilter.checkboxExpansion = [true, true, true, true]; //controls the open state of each panel. Add more items here when creating new expandable panels
                this.$refs.advancedFilter.flagExpansion = [true];
            }
            else if (this.variantTabActive == "tab-cnv") {
                this.currentFilterType = "cnv";
                this.$refs.advancedFilter.disableFiltering = false;
            }
            else if (this.variantTabActive == "tab-translocation") {
                this.currentFilterType = "ftl";
                this.$refs.advancedFilter.flagExpansion = [true];
                this.$refs.advancedFilter.disableFiltering = false;
            }
            else if (this.variantTabActive == "tab-virus") {
                this.currentFilterType = "virus";
                this.$refs.advancedFilter.flagExpansion = [true];
                this.$refs.advancedFilter.disableFiltering = true;
            }
            else { //no filter for translocation for now
                this.$refs.advancedFilter.disableFiltering = true;
            }
        },
        variantDetailsEndedLoading() {
            if (this.currentItem) {
                // this.currentItem.loading = false;
            }
        },
        getPatientDetailsFlexClass(item) {
            if (item && (item.field == "tumorTissueType" || item.field == "icd10")) {
                return 'xs12 lg10';
            }
            if (item && item.field == "icd10") {
                return 'xs12';
            }
            if (item && item.field == "oncotree") {
                return 'xs8 lg6 pt-0';
            }
            if (item && item.type) {
                return 'xs12 lg10';
            }
            return 'xs';
        },
        getPatientDetailsMarginClass(item) {
            if (item && (item.field == "tumorTissueType" || item.field == "icd10")) {
                return "mt-1";
            }
            return "";
        },
        handleSaveAll(autoSave) {
            if (autoSave) {
                this.waitingForAjaxMessage = "Work auto saved"
            }
            else {
                this.waitingForAjaxMessage = "Work saved";
                clearInterval(this.autoSaveInterval);
                this.createAutoSaveInterval();
            }
            this.waitingForAjaxCount = 0;
            if (this.variantDetailsUnsaved) {
                this.waitingForAjaxCount++;
            }
            if (this.annotationSelectionUnSaved) {
                this.waitingForAjaxCount++;
            }
            if (this.variantUnSaved) {
                this.waitingForAjaxCount++;
            }
            if (this.patientDetailsUnSaved) {
                this.waitingForAjaxCount++;
            }
            if (this.isCaseAnnotationChanged()) {
                this.waitingForAjaxCount++;
            }

            if (this.waitingForAjaxCount > 0) {
                this.waitingForAjaxActive = true;
                if (this.variantDetailsUnsaved && this.$refs.variantDetailsDialog) {
                    this.$refs.variantDetailsDialog.saveAllVariants(true).then(() => {
                        this.waitingForAjaxCount--;
                    });
                }
                if (this.annotationSelectionUnSaved && this.$refs.variantDetailsDialog) {
                    this.$refs.variantDetailsDialog.saveAllAnnotationSelections(true).then(() => {
                        this.waitingForAjaxCount--;
                    });
                }
                if (this.variantUnSaved) {
                    this.saveSelection(false, true)
                }
                if (this.patientDetailsUnSaved) {
                    this.savePatientDetails(true);
                }
                if (this.isCaseAnnotationChanged()) {
                    this.saveCaseAnnotations(true);
                }
            }
        },
        saveSelection(closeAfter, skipSnackBar) {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            if (this.saveLoading) {
                return;
            }
            this.saveLoading = true;

            let selectedSNPVariantIds = this.$store.getters["snpStore/getSelectedVariantIds"];
            let selectedCNVIds = this.$store.getters["cnvStore/getSelectedVariantIds"];
            let selectedTranslocationIds = this.$store.getters["ftlStore/getSelectedVariantIds"];
            let selectedVirusIds = this.$store.getters["virStore/getSelectedVariantIds"];
            axios({
                method: 'post',
                url: webAppRoot + "/saveVariantSelection",
                params: {
                    caseId: this.$route.params.id,
                    closeAfter: closeAfter, //pass this param along to proceed with closing the dialog
                    skipSnackBar: skipSnackBar //pass this param along to display snackbar after successful ajax call
                },
                data: {
                    selectedSNPVariantIds: selectedSNPVariantIds,
                    selectedCNVIds: selectedCNVIds,
                    selectedTranslocationIds: selectedTranslocationIds,
                    selectedVirusIds: selectedVirusIds
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    if (!response.data.skipSnackBar) {
                        this.showSnackBarMessage("Variant Selection Saved");
                    }
                    // this.getAjaxData();
                    this.waitingForAjaxCount--;
                    this.variantUnSaved = false;
                    this.saveLoading = false;
                    if (response.data.uiProceed) {
                        this.closeVariantDetails();
                        this.closeReviewDialog(true);
                    }
                    this.updateSelectedVariantTable();
                }
                else {
                    this.saveLoading = false;
                    this.waitingForAjaxCount--;
                    this.handleDialogs(response.data, this.saveSelection.bind(null, response.data.uiProceed, response.data.skipSnackBar));
                }
            }).catch(error => {
                this.saveLoading = false;
                this.handleAxiosError(error);
            });
        },
        savePatientDetails(skipSnackBar) {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            this.savingPatientDetails = true;
            axios({
                method: 'post',
                url: webAppRoot + "/savePatientDetails",
                params: {
                    oncotreeDiagnosis: this.patientDetailsOncoTreeDiagnosis.text,
                    tumorTissue: this.patientDetailsTumorTissue,
                    icd10: this.patientDetailsICD10,
                    dedupAvgDepth: this.patientDetailsDedupAvgDepth,
                    dedupPctOver100X: this.patientDetailsDedupPctOver100X,
                    tumorPercent: this.patientDetailsTumorPercent,
                    caseId: this.$route.params.id,
                    tmbClass: this.tmbClass,
                    msiClass: this.msiClass,
                    skipSnackBar: skipSnackBar
                }
            }).then(response => {
                this.waitingForAjaxCount--;
                if (response.data.isAllowed && response.data.success) {
                    this.patientDetailsUnSaved = false;
                    this.getPatientDetails();
                    if (!this.skipSnackBar) {
                        this.showSnackBarMessage("Patient Details Saved");
                    }
                }
                else {
                    this.handleDialogs(response.data, this.savePatientDetails);
                }
                this.savingPatientDetails = false;
            }).catch(error => {
                this.savingPatientDetails = false;
                this.handleAxiosError(error);
            });
        },
        updateEditAnnotationBreadcrumbs(visible) {
            this.urlQuery.edit = visible;
            // console.log("killing cnv plot");
            this.updateRoute();
        },
        breadcrumbNavigation(index) {
            //change the urlQuery based on walking up the breadcrumbs
            var goBack = this.breadcrumbs.length - index;
            for (var i = 0; i < goBack; i++) {
                var currentBreadcrumb = this.breadcrumbs.pop();
                for (var j = 0; j < currentBreadcrumb.params.length; j++) {
                    this.urlQuery[currentBreadcrumb.params[j]] = null;
                }
            }
            this.updateRoute();
        },
        disableBreadCrumbItem(item, index) {
            return (item.disabled || index == this.breadcrumbs.length - 1);
        },
        handleWaitingForAjaxCount() {
            if (this.waitingForAjaxActive && this.waitingForAjaxCount <= 0) {
                this.waitingForAjaxActive = false;
                this.saveAllNeeded = false;
                this.$store.commit("updateOpenCaseSaveNeeded", this.saveAllNeeded);
                this.showSnackBarMessage(this.waitingForAjaxMessage);
                clearInterval(this.autoSaveInterval);
                this.createAutoSaveInterval();
            }
        },
        createAutoSaveInterval() {
            this.autoSaveInterval = setInterval(() => {
                var editing = this.$route.query.edit === true || this.$route.query.edit === "true" || (this.$refs.caseNotes && this.$refs.caseNotes.isFocused);
                var reviewing = this.$route.query.showReview === true || this.$route.query.showReview === "true";
                if (!this.waitingForAjaxActive && !editing && !reviewing) {
                    this.handleSaveAll(true);
                }
            }, 120000);
        },
        openReport() {
            if (!this.reportReady) {
                return; //a disabled menu item will still call openReport. This should block it.
            }
            var path = webAppRoot + "/openReport";
            if (this.readonly) {
                path += "ReadOnly"
            }
            path += "/" + this.$route.params.id;
            router.push({ path: path });
        },
        getOpenReportHref() {
            if (!this.reportReady) {
                return "#"; //a disabled menu item will still call openReport. This should block it.
            }
            var path = webAppRoot + "/openReport";
            if (this.readonly) {
                path += "ReadOnly"
            }
            path += "/" + this.$route.params.id;
            return path;
        },
        toggleLookupTool() {
            if (this.$refs.lookupTool) {
                this.$refs.lookupTool.panelVisible = !this.$refs.lookupTool.panelVisible;
                this.$nextTick(() => {
                    this.$refs.lookupTool.currentlyActive = "Cancer";
                    this.$refs.lookupTool.currentOncotreeCode = this.patientDetailsOncoTreeDiagnosis;
                    if (this.$refs.lookupTool.isFormValid()) {
                        this.$refs.lookupTool.submitForm();
                    }
                });
            }
        },
        reloadLookupValues(ref, cnvGeneName, activeButton) {
            if (!ref) {
                ref = this.$refs.lookupTool;
            }
            if (ref.currentlyActive && !activeButton) {
                activeButton = ref.currentlyActive;
            }
            let oncotreeItems = oncotree.filter(o => o.text == this.patientDetailsOncoTreeDiagnosis.text);
            let oncotreeCode = null;
            if (oncotreeItems && oncotreeItems[0]) {
                oncotreeCode = oncotreeItems[0];
            }
            ref.currentOncotreeCode = oncotreeCode;
            ref.submitForm();
        },
        isLookupVisible() {
            return this.$refs.lookupTool && this.$refs.lookupTool.panelVisible;
        },
        addOtherAnnotatorSelection(type, annotatorId, variantIds) {
            if (this.$refs.reviewDialog)
                this.$refs.reviewDialog.startLoading();
            this.waitingForAjaxActive = true;
            //update dicts of selected from selectionPerAnnotator
            if (type) {
                for (let i = 0; i < variantIds.length; i++) {
                    this.handleSelectionChangeFromVariantDetails(variantIds[i], type.toUpperCase(), true);
                }
            }
            this.getAjaxData().then(response => {
                this.updateSelectedVariantTable();
                this.variantUnSaved = true;
                this.waitingForAjaxActive = false;
            }).catch(error => {
                this.handleDialogs(error.data, this.addOtherAnnotatorSelection.bind(this, type, annotatorId, variantIds));
            });
        },
        copyLink() {
            if (this.$refs.copyTextField)
                this.$refs.copyTextField.focus();
            document.execCommand("copy");
            this.showSnackBarMessage("Text copied!");
        },
        closeCopyDialog() {
            this.copyDialogVisible = false;
            this.toggleHTMLOverlay();
        },
        openLink(link) {
            window.open(link, "_blank", 'noopener');
        },
        autoSelectVUSs() {
            if (this.$refs.reviewDialog) {
                this.$refs.reviewDialog.autoVUSLoading = true;
                axios.get(
                    webAppRoot + "/selectVUSAnnotations",
                    {
                        params: {
                            caseId: this.$route.params.id
                        }
                    })
                    .then(response => {
                        if (response.data.isAllowed && response.data.success) {
                            console.log(response.data.payload);
                            if (this.$refs.reviewDialog)
                                this.$refs.reviewDialog.openAutoSelectVUSSummary(response.data.payload);
                        }
                        else {
                            if (this.$refs.reviewDialog)
                                this.$refs.reviewDialog.autoVUSLoading = false;
                            this.handleDialogs(response.data, this.selectVUSAnnotations);
                        }
                    }).catch(error => {
                        if (this.$refs.reviewDialog)
                            this.$refs.reviewDialog.autoVUSLoading = false;
                        this.handleAxiosError(error);
                    });
            }
        },
        openAddFusionDialog() {
            this.addFusionDialogVisible = true;
        },
        closeAddFusionDialog() {
            this.addFusionDialogVisible = false;
        },
        getPatientDetailLabelClass(item) {
            var classes = ['text-xs-left', 'grow'];
            if (item && item.type == 'text-field') {
                classes.push('pt-2');
            }
            if (item && item.field == 'icd10') {
                classes.push('xs2');
            }
            else if (item && item.field == 'msi') {
                classes.push('xs');
            }
            else {
                classes.push('xs5');
            }
            return classes;
        }
    },
    mounted() {
        bus.$emit("need-layout-resize");
        this.mountComponent();
        this.loadFromParams();
        this.updateBreadcrumbs();

    },
    created() {
        this.createAutoSaveInterval();
    },
    computed: {
        webAppRoot() {
            return webAppRoot;
        },
        annotationSelectionUnSaved() {
            return this.$store.getters["annotationStore/getNeedSaving"];
        },
        variantDetailsUnsaved() {
            return this.$store.getters["variantStore/getNeedSaving"]
        },
        institutionName() {
            return institutionName;
        }
    },
    beforeDestroy() {
         clearInterval(this.autoSaveInterval);
    },
    destroyed: function () {
       
    },
    watch: {
        '$route': "handleRouteChanged",
        variantTabActive: "handleTabChanged",
        waitingForAjaxCount: "handleWaitingForAjaxCount",
    },
};
