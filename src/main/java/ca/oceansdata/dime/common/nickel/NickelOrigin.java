package ca.oceansdata.dime.common.nickel;

/** Defines which service sent the Nickel,
 *  this is known as the origin.
 *
 *
 */
public enum NickelOrigin {
    DIME_GATEWAY,
    ORCID_SERVICE,
    DOCUMENT_SERVICE,
    METADATA_SERVICE,
    INTEGRATION_SCHEMA_SERVICE,
    CSV_PARSER,
    HISTORY_SERVICE,
    TASK_SERVICE, //TODO - rename Matching Service to Task Service
    CSV_INTEGRATION_SERVICE,
    DIME_TOOLS
}
